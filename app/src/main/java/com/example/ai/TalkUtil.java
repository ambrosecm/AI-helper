package com.example.ai;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TalkUtil {
//	private static String url = "https://api.tianapi.com/txapi/robot/index?key=e7e1b90a247ebc430727cb16f4fe98ba&question=!!";
	private static String url = "https://api.qingyunke.com/api.php?key=free&appid=0&msg=!!";
	 
    public String getMessage(String msg) throws JSONException {
    	System.out.println(111);
        return getString(getHTML(getUrl(msg)));
    }
	 public static String getUrl(String question){
		 String real_url = url.replace("!!",question);
		 return real_url;
	 }
	 
	 private String getHTML(String url) {
		 String result = "";
		 System.out.println("------------url = " + url);
		 InputStream is = null;
		 ByteArrayOutputStream baos = null;
		 try {
			 URL urls = new URL(url);
			 HttpURLConnection connection = (HttpURLConnection) urls
					 .openConnection();
			 connection.setReadTimeout(5 * 1000);
			 connection.setConnectTimeout(5 * 1000);
			 connection.setRequestMethod("GET");

			 is = connection.getInputStream();
			 baos = new ByteArrayOutputStream();
			 int len = -1;
			 byte[] buff = new byte[1024];
			 while ((len = is.read(buff)) != -1) {
				 baos.write(buff, 0, len);
			 }
			 baos.flush();
			 result = new String(baos.toByteArray());
		 } catch (Exception e) {
			 e.printStackTrace();
		 } finally {
			 if (is != null) {
				 try {
					 is.close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }

			 if (baos != null) {
				 try {
					 baos.close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 if(result=="") {
			 System.out.println("-------false--------");
		 }
		 return result;
	    }
	    
	 private String getString(String json) throws JSONException {

	        	JSONObject object = new JSONObject(json);
	            return (String) object.get("content");


	    }
}
