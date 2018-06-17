package crawl.service;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static crawl.util.JsoupUtil.getDocument;

public class ipSpider {
	
	//获取ip数量
	static final int IPCOUNT = 50;
	static String  proxyHost = "http://www.xicidaili.com/nn/1";
	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
	static String testUrl = "http://ip.chinaz.com/getip.aspx";
	static String rootPath = System.getProperty("user.dir");
	
	// 获取速度较快的可用代理并保存到文件proxy.txt
	public static void getProxyIp() {
		try {
			
			int ipCount = 0;
			BufferedWriter proxyIpWriter = new BufferedWriter(new FileWriter(rootPath + "/src/main/resources/proxyip.txt"));
			Document doc = getDocument(proxyHost);
			Elements ips = doc.select("#ip_list tr"); 
			System.out.println(ips.size());
			
			for (Element e : ips) {
				Elements ip = e.select("td");
				if (ip.size() > 2){
					String ipAddr = ip.get(1).text();
					int port = Integer.parseInt(ip.get(2).text());
					if (testIp(ipAddr,port)) {
						System.out.println(ipAddr + ":" + port + "  可用");
						proxyIpWriter.write(ipAddr + ":" + port);
						proxyIpWriter.newLine();
						ipCount++;
					}
				}
				if(ipCount >= IPCOUNT)
					break;
			}
			proxyIpWriter.flush();
			proxyIpWriter.close();
		} catch (IOException e) {
			// TODO: handle exception'
			e.printStackTrace();
		}
	}
	
	// 测试代理ip的速度
	public static boolean testIp(String ip, int port) {
		try {
			
		
			Document doc = Jsoup.connect(testUrl)
		            .userAgent(userAgent)
		            .proxy(ip, port)
		            .timeout(10000)
		            .get();
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("链接超时");
			return false;
			
		}
	}
	
	public static void main(String[] args) {
		getProxyIp();
		System.out.println("成功获取可用代理ip");
	}
	
}
