package crawl.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class testDbConnect {
public static void main(String[] args) {
		
		String jdbcName = "com.mysql.cj.jdbc.Driver";
		String dbUrl = "jdbc:mysql://localhost:3306/crawl_news?serverTimezone=UTC";
		//这里有一个坑，mysql 6.0.6 �?要在数据库连接url后面加上 �??serverTimezone=UTC�?
		String username = "root";
		String password = "root";
		// TODO Auto-generated method stub
		try {  
			Class.forName(jdbcName);
            System.out.println("驱动加载成功");  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
            System.out.println("驱动加载失败");  
        }  
          
        Connection con = null;  
        try {  
            con = DriverManager.getConnection(dbUrl,username,password);
            System.out.println();
            System.out.println("数据库连接成�?");  
            System.out.println("下面进行操作");  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }finally {  
            try {  
                con.close();  
                System.out.println("已经释放");  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
	}
}
