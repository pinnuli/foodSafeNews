package foodSafetyNews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestFrontDay {

	public static void main (String[] args) throws Exception{
		// TODO Auto-generated method stub
		String yesterday = getFrontDay("2018-6-17");
		System.out.println(yesterday);
	}
	
	public static String getFrontDay(String date) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();//获取日历实例  
		calendar.setTime(sdf.parse(date)); 
		int day = calendar.get(Calendar.DATE); 
		calendar.set(Calendar.DATE, day-1);  //设置为前一天
		String yesterday= sdf.format(calendar.getTime());//获得前一天
		return yesterday;
	}
}
