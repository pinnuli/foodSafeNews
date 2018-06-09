package dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import model.Article;

public class TestDao {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		ArticleDao articleDao = new ArticleDao();
		/*JSONArray articles = articleDao.query(2);*/
		int count = articleDao.getTotalCount();
		System.out.println(count);
	}
}
