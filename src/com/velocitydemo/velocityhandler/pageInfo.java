package com.velocitydemo.velocityhandler;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
 



import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import sun.misc.BASE64Encoder;


public class pageInfo {
	
	public pageInfo(String path){
		File f = new File(path + File.separator +"page.txt");
		//System.out.println("where is it? :"+ path  + File.separator +  "store.txt");//在屏幕上输出
		if(f.exists()){
			try {
	            FileReader fr = new FileReader(path + File.separator + "page.txt");
	            BufferedReader br = new BufferedReader(fr);  
	            String myreadline;

                myreadline = br.readLine();//读取一行
                //System.out.println(myreadline);//在屏幕上输出
                JSONObject jsonObject = JSONObject.fromObject( myreadline );


            	confluencesite = jsonObject.get("confluencesite").toString();
            	summary = jsonObject.get("summary").toString();
            	//contentbody = jsonObject.get("contentbody").toString();
            	username = jsonObject.get("username").toString();
            	password = jsonObject.get("password").toString();
            	pageId = jsonObject.get("pageId").toString();
            	siteUrl = jsonObject.get("siteUrl").toString();
            	//System.out.println("siteUrl: "+siteUrl);
	            br.close();
	            fr.close();
         
			}			        
			catch (IOException e) {
	            e.printStackTrace();
	        }
			
		}
	}
	
	public String getConfluencesite() {
		return confluencesite;
	}
	public void setConfluencesite(String confluencesite) {
		this.confluencesite = confluencesite;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String confluencesite;
	private String summary;
	private String contentbody;
	public String getContentbody() {
		return contentbody;
	}

	public void setContentbody(String contentbody) {
		this.contentbody = contentbody;
	}
	private String username;
	private String password;
	private String jsonStr;
	private String pageId;
	private String siteUrl;
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public String getTSiteUrl() {
		
		return siteUrl;
	}
	private final String USER_AGENT = "Mozilla/5.0";
	
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public String getJsonStr() {
		return jsonStr;
	}
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}
	private String createJson(){
        JSONObject outJson = new JSONObject();  

        outJson.put("confluencesite", confluencesite);
        outJson.put("summary", summary);
        //outJson.put("contentbody", contentbody);
        outJson.put("username", username);
        outJson.put("password", password);
        outJson.put("pageId", pageId);
        outJson.put("siteUrl", siteUrl);
        return outJson.toString();
	}
	
	public void getPage(String confluencesite, String summary,  String username, String password){
		this.confluencesite = confluencesite;
		this.summary = summary;
		//this.contentbody = contentbody;
		this.username = username;
		this.password = password;
		getSiteUrl();
		try {
			this.pageId = getPageInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.jsonStr = createJson();
	}
	
	public void writeFile(String path) {  
		
			try {
				FileWriter fw = new FileWriter(path + File.separator +"page.txt");
				BufferedWriter bw = new BufferedWriter(fw);   
	
				bw.write(jsonStr); //写入文件
	            bw.flush();    //刷新该流的缓冲
	            bw.close();
	            fw.close();
			}catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}
	
	private void getSiteUrl(){
		Pattern pattern = Pattern.compile("^(.+?)/display.*");
		Matcher matcher = pattern.matcher(confluencesite);
		if(matcher.find()){
		  System.out.println(matcher.group(1));
		  siteUrl = matcher.group(1);
		}
	}
	
	
	private String getPageInfo() throws IOException{
		 
						
		URL obj = new URL(confluencesite);
		//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection  con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		BASE64Encoder base64Encoder = new BASE64Encoder();
        String authStr = username + ":" + password;
        String authEnc = base64Encoder.encode(authStr.getBytes());
        con.setRequestProperty("Authorization", "Basic " + authEnc);


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + confluencesite);
		System.out.println("Response Code : " + responseCode);
		
		StringBuffer response = new StringBuffer();
		String inputLine;
		
		Pattern pattern = Pattern.compile("pageId=(.+?)\"");

		String pageid = "";
		try{	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			Matcher matcher = pattern.matcher(response);
			if(matcher.find()){
			  System.out.println(matcher.group(1));
			  pageid = matcher.group(1);
			}
			
		} catch(Exception e){
			
			e.printStackTrace();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		 
		//print result
		System.out.println("Response String : " +response.toString());
		
		return pageid;
	}
	
	public String getPageBody() throws IOException{
		
		URL obj = new URL(siteUrl+"/rest/prototype/1/content/"+pageId);
		//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection  con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		BASE64Encoder base64Encoder = new BASE64Encoder();
       String authStr = username + ":" + password;
       String authEnc = base64Encoder.encode(authStr.getBytes());
       con.setRequestProperty("Authorization", "Basic " + authEnc);


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + confluencesite);
		System.out.println("Response Code : " + responseCode);
		
		StringBuffer response = new StringBuffer();
		String inputLine;
		
		Pattern pattern = Pattern.compile("<body type=\"2\">(.+?)</body>");
		//System.out.println("<h1>"+summary+"</h1>");
		Pattern patterntitle = Pattern.compile("<h1>"+summary+"</h1>(.+?)<h1>");
		Pattern patterntitle2 = Pattern.compile("<h1>"+summary+"</h1>(.+?)$");
		String body = "";
		String bodytitle = "";
		try{	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			Matcher matcher = pattern.matcher(response);
			if(matcher.find()){
			  
			  body = StringEscapeUtils.unescapeHtml(matcher.group(1));
			  //System.out.println(body);
			  Matcher matchertitle = patterntitle.matcher(body);
				if(matchertitle.find()){
				  System.out.println(matchertitle.group(1));
				  bodytitle = matchertitle.group(1);
				}else{
					Matcher matchertitle2 = patterntitle2.matcher(body);
					if(matchertitle2.find()){
					  System.out.println(matchertitle2.group(1));
					  bodytitle = matchertitle2.group(1);
					}
				}
			}
			
		} catch(Exception e){
			
			e.printStackTrace();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		 
		//print result
		//System.out.println("Response String : " +response.toString());
		
		return bodytitle;
	}
	
}
