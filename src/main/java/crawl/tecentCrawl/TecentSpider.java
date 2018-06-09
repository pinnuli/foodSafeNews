package crawl.tecentCrawl;

import static crawl.util.JsoupUtil.getDocument;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawl.service.SpiderBase;
import dao.ArticleDao;
import model.Article;

public class TecentSpider extends SpiderBase{
	//接口前后缀
	static String urlPrefix = "http://api.shenjian.io/?appid=6caf52952fd5348bd2e72b1c08ef7266";
	static String urlPostfix = "";
	
	@Override
	public void getAllUrls(){
			
		List<String> dateEntryArray = new ArrayList<String>();
		dateEntryArray = getDateEntry(0);
		for (int i = 0; i < dateEntryArray.size(); i++) {
			getDateUrl(dateEntryArray.get(i));
			/*System.out.println(dateEntryArray.get(i));*/
		}
	}
	
	@Override
	public void getDateUrl(String dateEntryUrl) {
		List<String> dateurls = new ArrayList<String>();
		
		try {
			Document doc = getDocument(dateEntryUrl);
			String jsonHtml = doc.select("body").text();
			JSONObject data = new JSONObject(jsonHtml);
			JSONArray articlesArray = data.getJSONArray("data");
			for(int i = 0;i<articlesArray.length();i++){
				String url = articlesArray.getJSONObject(i).getString("url");
				try {
					urlQueue.put(url);
					System.out.println("url: " + url + "已加入新闻队列");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public  List<String> getDateEntry(int col){
		List<String> dateEntryArray = new ArrayList<String>();
		List<String> threeMonthDates = getThreeMonths();
		for (int i = 0; i < threeMonthDates.size(); i++) {
			String dateEntryUrl = urlPrefix ; 
			dateEntryUrl += ("&date=" + threeMonthDates.get(i));
			dateEntryUrl += urlPostfix;
			System.out.println("获取" + threeMonthDates.get(i) + "入口" + dateEntryUrl + "成功！");
			dateEntryArray.add(dateEntryUrl);
		}
		return dateEntryArray;
	}
	
	@Override
	public void crawler(String url) {
		Document doc = getDocument(url);
		
		//腾讯的新闻url中间是格式为xxxxxxx的日期
		String pattern = ".*/\\d{8}/.*";
		if(Pattern.matches(pattern, url)) {
			String title = "";
			String content = "";
			String category = "";
			String srcFrom = "";
			String articleUrl = "";
			String pubTime = ""; 
			String orignalDate = "";
			String articleInfo = "";
			Article article = new Article();
			try {
				
				title = doc.select(".qq_article h1").text();
				category = doc.select(".a_catalog a").text();
				srcFrom = doc.select(".a_source a").text();
				pubTime = doc.select(".a_time").text();
				
				Elements paras = doc.body().select(".bd p");
				if(!paras.isEmpty()) {
					for (Element element : paras) {
						content += element.text();
						content += "\n";
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				return;
			}
			
			
			
			article.setArticleUrl(url);
			article.setSrcFrom(srcFrom);
			article.setCategory(category);
			article.setTitle(title);
			article.setPubTime(pubTime);
			article.setContent(content);
			
			
			//打印一下新闻信息
			System.out.println("爬取成功：" + article);
			//添加到数据库
			try {
				ArticleDao dao = new ArticleDao();
				dao.addArticle(article);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
