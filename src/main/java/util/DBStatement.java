package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBStatement {
	
	//jdbc配置参数
	private static final String URL = "jdbc:mysql://localhost:3306/crawl_news?serverTimezone=UTC";
	// 这里有一个坑，mysql 6.0.6 需要在数据库连接url后面加上 “?serverTimezone=UTC”
	private static final String jdbcName = "com.mysql.cj.jdbc.Driver";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	private static Connection conn = null;
	
	static {
		try {
			
			Class.forName(jdbcName);
			System.out.println("驱动加载成功");
			//得到连接数据库的对象
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		return conn;
	}	
	
	/* public static void main(String[] args) {

		try {
			Connection conn = DBStatement.getConnection();
			if (conn != null) {
				System.out.println("数据库连接正常！");
			} else {
				System.out.println("数据库连接异常！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/
}
