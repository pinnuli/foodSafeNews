package crawl.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import crawl.util.HttpUtil;

//如果请求次数超过10，则放弃抓取，避免url出错一直等待
public class JsoupUtil {

	static Document doc = null;
	static int requestCount = 0;
	
	// 判断是否抓取网页成功，成功则返回，否则重置代理ip
	public static Document getDocument(String url) {
		doc = get(url);
		if ((doc == null || doc.toString().trim().equals("")) && requestCount < 10) {
			requestCount++;
			System.out.println("出现ip被拦截");
			HttpUtil.setProxyIp();
			get(url);
		}
		return doc;
		
	}
	
	//抓取网页内容并返回
	public static Document get(String url) {
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
		try {
			doc = Jsoup
					.connect(url)
					.userAgent(userAgent)
					.timeout(100000)
					.ignoreContentType(true)
					.get();
			// 此处trim()返回一个字符串，其值为此字符串，并删除任何前导和尾随空格
			
			return doc;

		} catch (Exception e) {
			// TODO: handle exception
			if (requestCount < 10) {
				requestCount++;
				System.out.println("链接超时");
				HttpUtil.setProxyIp();
				get(url);
			} else {
				System.out.println("请求次数已超过10次，放弃抓取此新闻！");
				return doc;
			}
		}
		return doc;
	}
	/*public static void main(String[] args) {
		String hostUrl = "http://news.sohu.com/s2005/wangzhanditu.shtml";
		Document doc = getDocument(hostUrl);
		Elements urls = doc.select("div table tbody tr td div font");
		
		for (Element url : urls) {
			System.out.println(url);
		}
	}*/
	
}

