package crawl.sinaCrawl;

import static crawl.util.JsoupUtil.getDocument;

import java.sql.Statement;
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
import crawl.util.Recognition;
import dao.ArticleDao;
import model.Article;

public class SinaSpider extends SpiderBase {

	static String urlPrefix = "http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?";
	// 滚动新闻首页，即导航页
	static String urlPostfix = "&ch=01&k=&offset_page=0&offset_num=0&spec=&type=&asc=&page=1&r=0.7635779715100255";
	
	
	@Override
	public void getAllUrls() {
		// 按照新浪滚动页面的分类，从90-99分别代表不同分类
		System.out.println("现在开始爬取" + currentDate + "的新闻！");
		for (int col = 90; col <= 99; col++) {
			String dateEntryUrl = "";
			dateEntryUrl = getDateEntry(col);
			getDateUrl(dateEntryUrl);
		}
	}

	@Override
	public void getDateUrl(String dateEntryUrl) {
		try {
			Thread.currentThread().sleep(random.nextInt(1000));
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		List<String> dateurls = new ArrayList<String>();
		try {
			Document doc = getDocument(dateEntryUrl);
			String jsonDataName = "var jsonData = ";
			String jsonHtml = doc.select("body").text().replace(jsonDataName, "");
			System.out.println(jsonHtml);
			JSONObject data = new JSONObject(jsonHtml);
			JSONArray articlesArray = data.getJSONArray("list");
			for (int i = 0; i < articlesArray.length(); i++) {
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
	public String getDateEntry(int col) {
		String dateEntryUrl = urlPrefix;
		dateEntryUrl += ("&col=" + col);
		dateEntryUrl += ("&date=" + currentDate);
		dateEntryUrl += "&num=200";
		dateEntryUrl += urlPostfix;
		System.out.println("获取" + currentDate + "入口" + dateEntryUrl + "成功！");
		return dateEntryUrl;
	}

	@Override
	public void crawler(String url) {
		Document doc = getDocument(url);
		System.out.println(doc);
		// 新浪的新闻url中间是格式为xxxx-xx-xx的日期
		String pattern = ".*/\\d{4}-\\d{2}-\\d{2}/.*";
		if (Pattern.matches(pattern, url)) {
			String title = "";
			String content = "";
			String category = "";
			String srcFrom = "";
			String pubTime = "";
			String orignalDate = "";
			String articleInfo = "";
			Article article = new Article();
			System.out.println("正则解析：" + url);
			try {
				// 新浪新闻有时候开头有两个h1，有时只有一个，最后一个为新闻标题
				Elements h1 = doc.select(".main-content h1");
				
				if (!h1.isEmpty()) {
					title = h1.get(h1.size() - 1).text();
					System.out.println(title);
					//if (Recognition.isSafe(title)) {//如果是食品安全相关则继续解析并放入数据库
						category = doc.select(".channel-path a").first().text();
						articleInfo = doc.select(".date-source").text();

						// 新闻发布日期和来源放在一个div中，有时是两个span，有时是一个 span和一个a
						// 所以这里直接取子串
						orignalDate = articleInfo.substring(0, 17);
						srcFrom = articleInfo.substring(18);

						// 格式化新闻发布时间
						String regex = "[\u4e00-\u9fa5]";
						Pattern pat = Pattern.compile(regex);
						Matcher mat = pat.matcher(orignalDate);
						pubTime = mat.replaceAll("-");
						Elements paras = doc.body().select("div.article p");
						if (!paras.isEmpty()) {
							for (Element element : paras) {
								content += element.text();
								content += "\n";
							}
						}

						article.setArticleUrl(url);
						article.setSrcFrom(srcFrom);
						article.setCategory(category);
						article.setTitle(title);
						article.setPubTime(pubTime);
						article.setContent(content);

						// 打印一下新闻信息
						System.out.println("爬取成功：" + article);

						try {
							ArticleDao dao = new ArticleDao();
							dao.addArticle(article);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						System.out.println("食品安全相关，成功添加到数据库！");
					//}
				}

			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				return;
			} 

		}
	}
}
