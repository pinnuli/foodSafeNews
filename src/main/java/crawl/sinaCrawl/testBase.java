package crawl.sinaCrawl;

public class testBase {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		SinaSpider sinaSpider = new SinaSpider();
		/*sinaSpider.crawler("http://news.sina.com.cn/o/2018-05-29/doc-ihcffhsu7400149.shtml");*/
		//sinaSpider.crawler("http://www.scau.edu.cn/2018/0629/c1300a90154/page.htm");
		sinaSpider.start();
	}
}
