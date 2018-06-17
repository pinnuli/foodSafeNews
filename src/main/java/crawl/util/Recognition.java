package crawl.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Recognition {

	// 判断模型接口
	static final String REQUESTURL = "http://bringtree.ddns.net:5000/news_recognition";

	//向判定模型发出http请求，接收判定结果并返回
	public static boolean isSafe(String title) {
		HttpURLConnection con = null;
		String param = "news_title=" + title;
		try {
			URL u = null;
			u = new URL(REQUESTURL);
			con = (HttpURLConnection) u.openConnection();
			// POST 只能为大写，严格限制，post会不识别
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(param);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject response = new JSONObject(buffer.toString());
		int code = response.getInt("code");
		return (code==1) ? true : false;
	}
	
}
