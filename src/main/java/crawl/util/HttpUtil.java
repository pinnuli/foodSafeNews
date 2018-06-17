package crawl.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//设置代理ip
public class HttpUtil {
	
	//获取项目路径
	static String rootPath = System.getProperty("user.dir");
	
	public static void setProxyIp() {
		try{
			List<String> ipList = new ArrayList<String>();
			BufferedReader proxyIpReader = new BufferedReader(new FileReader(rootPath + "/src/main/resources/proxyip.txt"));
			String ip = "";
			while((ip = proxyIpReader.readLine()) != null) {
				
				ipList.add(ip);
			}
			proxyIpReader.close();
			// 生成随机数构造器，随机获取代理ip
			Random random = new Random();
			int randomInt = random.nextInt(ipList.size());
			String ipPort = ipList.get(randomInt) ;
			String proxyIp = ipPort.substring(0, ipPort.lastIndexOf(":"));
			String proxyPort =ipPort.substring(ipPort.lastIndexOf(":") + 1, ipPort.length());
			System.setProperty("http.maxRedirects", "50");
			System.getProperties().setProperty("http.proxyHost", proxyIp);
			System.getProperties().setProperty("http.proxyPort", proxyPort);
			
			System.out.println("设置代理ip为：" + proxyIp + ":" + proxyPort);
	
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("代理异常，重新设置代理ip");
			e.printStackTrace();
			setProxyIp();
		}
		
	}
}
