package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import dao.ArticleDao;

public class ArticlesListServlet extends HttpServlet {
	ArticleDao articleDao = new ArticleDao();
	public ArticlesListServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		try {
			int page = Integer.parseInt(request.getParameter("page").toString());
			/*System.out.println(request.getParameter("page"));*/
			JSONArray result = articleDao.query(page);
			int totalCount = articleDao.getTotalCount();
			JSONObject articlesList = new JSONObject();
			articlesList.put("code", 200);
			articlesList.put("totalCount", totalCount);
			articlesList.put("data", result);
			out.println(articlesList);
		} catch (Exception e) {
			JSONObject errmsg = new JSONObject();
			errmsg.put("code", 500);
			errmsg.put("errmsg", "get data error!");
			e.printStackTrace();
			out.print(errmsg); 
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
}
