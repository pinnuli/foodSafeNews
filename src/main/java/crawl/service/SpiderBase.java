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
import java.util.Random;
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
	protected static BloomFilter filter = new BloomFilter();
	
	protected static Random random = new Random();
	
	protected static String urlPrefix = "";
	
	protected static String urlPostfix = "";
	
	protected static String currentDate = "";
	//设置爬取深度
	static int DEFAULT_DEPTH = 10;
		
	//设置最大线程数
	static int 	DEFAULT_THREAD_NUM = 10;
	
	//线程池
	static Executor executor = Executors.newFixedThreadPool(20);
	
	//数据库
	static Connection conn = DBStatement.getConnection();
	static PreparedStatement ps = null;
	
	//开始爬虫
	public void start() throws Exception{
		//日期初始化为当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();//获取日历实例  
		Date d = new Date();  
		Date dBefore = new Date();
		calendar.setTime(d);//把当前时间赋给日历  
        calendar.add(Calendar.MONTH, 0);  //设置为前3月  
        dBefore = calendar.getTime();   //得到前3月的时间  
        currentDate = sdf.format(dBefore);    
		getStart(); 
		mutilThreads();
		listenThreads();
		listenEnd();
		
	}
	
	//检查是否已经有爬过的url
	public void getStart() throws Exception{
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
			while(urlQueue.size() < 200) {
				setFrontDay();
				getAllUrls();
			}
		}
	}
	
	//开启多个线性进行爬网页
	public void mutilThreads() throws Exception{
		for (int i = 0; i < DEFAULT_THREAD_NUM; i++) {
			Thread a = new Thread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					try {
						while(true) {
							String url = getAUrl();
							if(!filter.contains(url)) {
								filter.add(url);
								System.out.println("线程：" + Thread.currentThread().getName() + "正在解析： " + url);
								if(url != null) {
									crawler(url);
									Thread.currentThread().sleep(1000);
								}
							}else {
								System.out.println("此url： " + url + "已存在，不再重复爬取！");
								
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
			});
			executor.execute(a);
		}
	}; 
	
	// 监视线程
	public void listenThreads() throws Exception {

		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						if (((ThreadPoolExecutor) executor).getActiveCount() < 10) {
							Thread a = new Thread(new Runnable() {

								public void run() {
									// TODO Auto-generated method stub
									while (true) {
										try {
											String url = getAUrl();
											if (!filter.contains(url)) {
												filter.add(url);
												System.out.println(
														"线程：" + Thread.currentThread().getName() + "正在抓取： " + url);
												if (url != null) {
													crawler(url);
												}
											} else {
												System.out.println("此url： " + url + "已存在，不再重复爬取！");
											}
										} catch (Exception e) {
											// TODO: handle exception
											e.printStackTrace();
										}

									}
								}
							});
							executor.execute(a);
						}
						Thread.sleep(3000);
					} catch (InterruptedException e) {
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
	
	//获取当天所有新闻url
	public abstract void getAllUrls() ;
	
	// 获取当天的分类新闻每一天的入口
	public abstract String getDateEntry(int col) ;
	
	// 获取分类新闻每天的url，解析json数组，并放入阻塞队列
	public abstract void getDateUrl(String dateEntryUrl) ;
	
	
	
	//设置日期，返回当前日期后并将当前日期设置为前一天
	public static void setFrontDay() throws Exception{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();//获取日历实例  
			calendar.setTime(sdf.parse(currentDate)); 
			int day = calendar.get(Calendar.DATE); 
			calendar.set(Calendar.DATE, day-1);  //设置为前一天
			currentDate= sdf.format(calendar.getTime());//获得前一天
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public String getAUrl() throws Exception{
		String url = "";
		//如果列表为空了就设置日期为当前的前一天
		if(urlQueue.size() == 0) {
			System.out.println(currentDate + "的新闻已爬完！");
			//线程休眠时间随机，避免多线程同时请求导致请求频率超过限制
			Thread.currentThread().sleep( random.nextInt(500)+500);
			while(urlQueue.size() < 10000) {
				setFrontDay();
				getAllUrls();
			}
		}
		try {
			System.out.println("url队列中还有" + urlQueue.size());
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
