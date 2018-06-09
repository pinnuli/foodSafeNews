package crawl.service;



import crawl.filter.*;
import crawl.util.*;
import dao.*;
import model.*;
import util.DBStatement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public abstract class SpiderBase implements Serializable{
	
	//url阻塞队列
	protected static BlockingQueue<String> urlQueue = null;
	
	//布隆算法去重
	static BloomFilter filter = new BloomFilter();
	
	static String urlPrefix;
	
	static String urlPostfix;
	
	//设置爬取深度
	static int DEFAULT_DEPTH = 10;
		
	//设置最大线程数
	static int 	DEFAULT_THREAD_NUM = 10;
	
	//线程池
	static Executor executor = Executors.newFixedThreadPool(20);
	
	//数据库
	static Connection conn = DBStatement.getConnection();
	static PreparedStatement ps = null;
	
	public void start() {
		getStart();
		mutilThreads();
		listenThreads();
		listenEnd();
		
	}
	
	public void getStart() {
		File urlsSer = new File("urlQueue.ser");
		if(urlsSer.exists()) {
			try {
				//反序列化
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(urlsSer));
				//这里在序列化的时候是以一个队列的形式，因而只需读一个对象，并转换为BlockingQueue
				urlQueue = (BlockingQueue<String>) ois.readObject();
				System.out.println(urlQueue);
				ois.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		else {
			urlQueue = new LinkedBlockingQueue<String>();
			getAllUrls();
		}
	}
	//开启多个线性进行爬网页
	public void mutilThreads() {
		for (int i = 0; i < DEFAULT_THREAD_NUM; i++) {
			Thread a = new Thread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					while(true) {
						String url = getAUrl();
						if(!filter.contains(url)) {
							filter.add(url);
							System.out.println("线程：" + Thread.currentThread().getName() + "正在抓取： " + url);
							if(url != null) {
								crawler(url);
							}
						}else {
							System.out.println("此url： " + url + "已存在，不再重复爬取！");
						}
					}
				}
			});
			executor.execute(a);
		}
	}; 
	
	// 监视线程
	public void listenThreads() {
		
				new Thread(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						while(true) {
							try {
								if(((ThreadPoolExecutor)executor).getActiveCount() < 10) {
									Thread a = new Thread(new Runnable() {
										
										public void run() {
											// TODO Auto-generated method stub
											while(true) {
												String url = getAUrl();
												if(!filter.contains(url)) {
													filter.add(url);
													System.out.println("线程：" + Thread.currentThread().getName() + "正在抓取： " + url);
													if(url != null) {
														crawler(url);
													}
												}else {
													System.out.println("此url： " + url + "已存在，不再重复爬取！");
												}
											}
										}
									});
									executor.execute(a);
									if(urlQueue.size() == 0) {
										System.out.println("队列为0，新闻已爬完！");
									}
								}
								Thread.sleep(3000);
							}catch (InterruptedException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
				}).start();
	}; 
	
	//监听程序结束，程序结束时序列化urlQueue和filter
	public void listenEnd() {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println(urlQueue.isEmpty());
				try {
					if (!urlQueue.isEmpty()) {
						File urlFile = new File("urlQueue.ser");
						if (!urlFile.exists()) {
							urlFile.createNewFile();
						}
						// 序列化urlQueue
						ObjectOutputStream urlOs = new ObjectOutputStream(new FileOutputStream(urlFile));
						urlOs.writeObject(urlQueue);
						urlOs.close();
					}

					File bitFile = new File("bits.ser");
					if (!bitFile.exists()) {
						bitFile.createNewFile();
					}
					// 序列化bits
					ObjectOutputStream bitOs = new ObjectOutputStream(new FileOutputStream(bitFile));
					bitOs.writeObject(filter.getBitset());
					bitOs.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}));
				
	}; 
	
	//获取近三个月所有新闻url
	public abstract void getAllUrls() ;
	
	// 获取近三个月的分类新闻每一天的入口
	public abstract  List<String> getDateEntry(int col) ;
	
	// 获取分类新闻每天的url，解析json数组，并放入阻塞队列
	public abstract void getDateUrl(String dateEntryUrl) ;
	
	// 得到近三个月的日期列表：格式为2018-03-20
	public static List<String> getThreeMonths() {
		Calendar begin = Calendar.getInstance();
		begin.setTime(new Date());
		begin.add(Calendar.MONTH, -3);
		begin.add(Calendar.DATE, +1);
		Date result = begin.getTime();
		Calendar end = Calendar.getInstance();
		Long startTime = begin.getTimeInMillis();
		Long endTime = end.getTimeInMillis();
		Long oneDay = 1000 * 60 * 60 * 24l;
		List<String> dates = new ArrayList<String>();
		Long time = startTime;
		int i = 0;
		while(time <= endTime) {
			Date d = new Date(time);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			dates.add(i, df.format(d));
			i++;
			time += oneDay;
		}
		Date d = new Date(time);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(dates);
		return dates;
	}
	
	public String getAUrl() {
		String url = "";
		try {
			url = urlQueue.take();
			return url;
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return url;
	}
	
	// 爬取新闻网页内容
	public abstract void crawler(String url);
	
}