package crawl.filter;

public class TestBloom {

	private BloomFilter filter = new BloomFilter();
	private String[] urls = {
			"https://news.baidu.com/",
			"http://news.sohu.com/",
			"http://news.qq.com/",
			"http://news.sohu.com/",
			"http://news.sina.com.cn/",
			"http://www.xinhuanet.com/",
			"http://news.qq.com/"
	};
	
	public void testBloomFilter(){
		for(String url: urls) {
			if(filter.contains(url)) {
				System.err.println('"' + url + '"' + " 已经存在！");
			}else {
				filter.add(url);
				System.out.println("添加 " + '"' + url + '"' + " 到队列！");
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestBloom test = new TestBloom();
		test.testBloomFilter();
	}

}
