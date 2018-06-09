package model;

public class Article {
	String title = "";	//新闻标题
	String content = " ";	//新闻内容 
	String category = "";	//新闻分类
	String srcFrom = "";	//新闻来源
	String articleUrl = "";	//文章url
	String pubTime = "";	//新闻发布时间
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getCategory() {
		return category;
	}

	public String getSrcFrom() {
		return srcFrom;
	}
	
	public String getArticleUrl() {
		return articleUrl;
	}
	
	public String getPubTime() {
		return pubTime;
	}
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setSrcFrom(String srcFrom) {
		this.srcFrom = srcFrom;
	}
	
	public void setArticleUrl(String articleUrl) {
		this.articleUrl = articleUrl;
	}
	
	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}
	
	@Override
	public String toString() {
		return "News: {\n" +
				"		title:" + title  + "\n" +
				"		from:" + srcFrom  + "\n" +
				"		category:" + category  + "\n" +
				"		articleUrl:" + articleUrl  + "\n" +
				"		pubTime:" + pubTime  + "\n" +
				"		content:" + content  + "\n" +
				"		}"
				;
	}
}
