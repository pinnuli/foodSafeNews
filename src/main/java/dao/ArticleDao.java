package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.xdevapi.JsonArray;

import java.util.ArrayList;
import java.util.Date;


import model.*;
import util.*;
public class ArticleDao {
	static final int PAGENUM = 20;
	public void addArticle(Article article) throws Exception{
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Connection conn = DBStatement.getConnection();
		String sql="insert into articles_test " + 
				"(title,content,category,src_from,article_url,pub_time,is_safe,deleted_at,created_at,updated_at) " +
				"values (?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ptmt=conn.prepareStatement(sql);
		try {
			ptmt.setString(1, article.getTitle());
			ptmt.setString(2, article.getContent());
			ptmt.setString(3, article.getCategory());
			ptmt.setString(4, article.getSrcFrom());
			ptmt.setString(5, article.getArticleUrl());
			ptmt.setString(6, article.getPubTime());
			ptmt.setInt(7, 0);
			ptmt.setInt(8, 1);
			ptmt.setTimestamp(9, timestamp);
			ptmt.setTimestamp(10, timestamp);
			ptmt.execute();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public JSONArray query(int page) throws Exception{
		JSONObject jsonobj = new JSONObject();
		List<Article> result=new ArrayList<Article>();
		
		Connection conn=DBStatement.getConnection();
		String sql = "select * from food_safe_articles"
				+ " order by id limit " + 
				+ PAGENUM * (page-1) + "," + PAGENUM;
		
		PreparedStatement ptmt=conn.prepareStatement(sql);
		ResultSet rs=ptmt.executeQuery();
		Article a=null;
		while(rs.next()){
			a=new Article();
			a.setTitle(rs.getString("title"));
			a.setContent(rs.getString("content"));
			a.setCategory(rs.getString("category"));
			a.setArticleUrl(rs.getString("article_url"));
			a.setSrcFrom(rs.getString("src_from"));
			a.setPubTime(rs.getString("pub_time"));
			result.add(a);
		}
		
		return new JSONArray(result);
	}
	
	public int getTotalCount() throws Exception{
		int count = 0;
		Connection conn=DBStatement.getConnection();
		String sql = "select count(id) rows from food_safe_articles";
		PreparedStatement ptmt=conn.prepareStatement(sql);
		ResultSet rs=ptmt.executeQuery();
		while(rs.next()){
			count = rs.getInt("rows");
		}
		
		return count;
	}
}
