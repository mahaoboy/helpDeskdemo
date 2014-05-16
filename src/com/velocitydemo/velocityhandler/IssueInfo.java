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

import org.apache.tomcat.util.codec.binary.Base64;

import sun.misc.BASE64Encoder;



public class IssueInfo {
	private final String USER_AGENT = "Mozilla/5.0";
	
	public String getJirasite() {
		return jirasite;
	}

	public void setJirasite(String jirasite) {
		this.jirasite = jirasite;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIssuetype() {
		return issuetype;
	}

	public void setIssuetype(String issuetype) {
		this.issuetype = issuetype;
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

	private String jirasite;
	private String project;
	private String summary;
	private String description;
	private String issuetype;
	private String username;
	private String password;
	private String jsonStr;
	private String issueKey;
	private String issueLink;
	
	private String createJson(){
        JSONObject outJson = new JSONObject();  
        JSONObject fieldsj = new JSONObject();  
        JSONObject projectj = new JSONObject();
        JSONObject issuetypej = new JSONObject();
        
        issuetypej.put("name", issuetype);
        projectj.put("key", project);
        fieldsj.put("summary", summary);
        fieldsj.put("issuetype",issuetypej);
        fieldsj.put("project",projectj);
        fieldsj.put("description", description);
        
        outJson.put("fields", fieldsj);  
        
        return outJson.toString();
	}
	
	private String getJson(String jsonStr){
		String key = getKey(jsonStr);
		issueLink = getSiteLink("browse/" + key);
		return issueLink;
	}
	
	private String getKey(String response){
		JSONObject jsonObject = JSONObject.fromObject( response );
		
		issueKey = jsonObject.get( "key" ).toString();
		return issueKey;
	}
	
	private void addKey(){
		JSONObject jsonObject = JSONObject.fromObject( jsonStr );
		
		jsonObject.put( "issuekey", issueKey);
		jsonObject.put( "jirasite", getSiteLink(""));
		jsonObject.put( "issuelink", issueLink);
		jsonObject.put( "username", username);
		jsonObject.put( "password", password);
		jsonStr =  jsonObject.toString();
	}
	
	public String createIssue(String jirasite, String project, String summary, String description, String issuetype, String username, String password ) throws Exception{
		
		this.jirasite = jirasite;
		this.project = project;
		this.summary = summary;
		this.description =description;
		this.issuetype = issuetype;
		this.username = username;
		this.password = password;
		
		jsonStr = createJson();
		System.out.println("jsonStr:---: " + jsonStr);
		
		return sendPost();
	}
	
	public void createIssue(String project, String summary, String description, String issuetype, String username){
		
		this.project = project;
		this.summary = summary;
		this.description =description;
		this.issuetype = issuetype;
		this.username = username;
		System.out.println("jsonStr:---: ");
		jsonStr = createJson();
		System.out.println("jsonStr:---: " + jsonStr);
		
	}
	
	private String getSiteLink(String extraLink){
		Pattern pattern = Pattern.compile("/");
		String lastL = jirasite.substring(jirasite.length()-1); 
		Matcher matcher = pattern.matcher(lastL);
		boolean urlCheck= matcher.matches();
		String url = "";
		//System.out.println("Test Last Letter: ----- : "+ lastL);
		if (urlCheck){
			url = jirasite + extraLink;
			//System.out.println("Test url: ----- : "+ url);
		}else{
			url = jirasite + "/" + extraLink;
			//System.out.println("Test1 url: ----- : "+ url);
		}
		return url;
	}
	
	private Map<Object, Object> getIssueInfo(String jirasite, String issuekey, String usernameT, String passwordT) throws IOException{
		 
		String url = jirasite+"rest/api/2/issue/"+issuekey;
				
		URL obj = new URL(url);
		//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection  con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		BASE64Encoder base64Encoder = new BASE64Encoder();
        String authStr = usernameT + ":" + passwordT;
        String authEnc = base64Encoder.encode(authStr.getBytes());
        con.setRequestProperty("Authorization", "Basic " + authEnc);


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		StringBuffer response = new StringBuffer();
		String inputLine;
		
		
		try{	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
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
		JSONObject jsonObject = JSONObject.fromObject( response.toString() );
		
		
		HashMap<Object, Object> issueinfo = new HashMap<Object, Object>();
		
		JSONObject fieldsJ = JSONObject.fromObject(jsonObject.get( "fields" ));
		
		JSONObject statusJ = JSONObject.fromObject(fieldsJ.get( "status" ));
		Object[] fixVersionsJ = JSONArray.fromObject(fieldsJ.get( "fixVersions" ).toString()).toArray();
		JSONObject assigneeJ = JSONObject.fromObject(fieldsJ.get( "assignee" ));
		
		String statusNameJ = statusJ.get( "name" ).toString();			
		String displayNameJ = assigneeJ.get( "displayName" ).toString();
		
		String fixversionS = "";
		JSONObject fixVersionsJI;
	    for(int i=0;i<fixVersionsJ.length;i++){  
	    	if(!fixversionS.isEmpty())
	    		fixversionS += ",";
	        fixVersionsJI = JSONObject.fromObject(fixVersionsJ[i]);
	        fixversionS += fixVersionsJI.get("name");
	        System.out.println("\n fixversions: "+ fixversionS);  
	    }  
		
		issueinfo.put("assignee",  displayNameJ);
		issueinfo.put("status", statusNameJ);
		issueinfo.put("fixVersions", fixversionS);
		return issueinfo;
	}
	
	private String sendPost() throws Exception {
		 
		String url = getSiteLink("rest/api/2/issue/");
				
		URL obj = new URL(url);
		//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection  con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json");
		//con.setRequestProperty("data", jsonStr);
		
		BASE64Encoder base64Encoder = new BASE64Encoder();
        String authStr = username + ":" + password;
        String authEnc = base64Encoder.encode(authStr.getBytes());
        con.setRequestProperty("Authorization", "Basic " + authEnc);

		String urlParameters = jsonStr;
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		
		StringBuffer response = new StringBuffer();
		String inputLine;
		
		
		try{	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
					 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
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
		getKey(response.toString());
		getJson(response.toString());
		addKey();
		return issueKey;
	}
	
	public String getIssueLink(){
		return issueLink;
	}
	
	public void writeFile(String path) {  
		File f = new File(path + File.separator + "store.txt");
		//System.out.println("where is it? :"+ path + "/store.txt");//在屏幕上输出
		if(f.exists()){
			try {
	            FileReader fr = new FileReader(path + File.separator +"store.txt");
	            BufferedReader br = new BufferedReader(fr);  
	            String myreadline;

                myreadline = br.readLine();//读取一行
                System.out.println(myreadline);//在屏幕上输出

	            br.close();
	            fr.close();
	            try {
					FileWriter fw = new FileWriter(path + File.separator +"store.txt");
					BufferedWriter bw = new BufferedWriter(fw);   
					JSONObject jsonObject = JSONObject.fromObject( myreadline );
					JSONObject jsonStrO = JSONObject.fromObject( jsonStr );
					
			        Integer keyValue = Integer.parseInt(jsonObject.get( "currentkey" ).toString()) + 1;
					jsonObject.put(keyValue.toString(), jsonStrO);
					jsonObject.put("currentkey", keyValue);
					
					bw.write(jsonObject.toString()); //写入文件
		            bw.flush();    //刷新该流的缓冲
		            bw.close();
		            fw.close();
				}catch (IOException e) {
		            e.printStackTrace();
		        }
	            
			}			        
			catch (IOException e) {
	            e.printStackTrace();
	        }
			
		}else{
			try {
				FileWriter fw = new FileWriter(path + File.separator +"store.txt");
				BufferedWriter bw = new BufferedWriter(fw);   
				JSONObject jsonObject = JSONObject.fromObject( jsonStr );
				JSONObject allItem = new JSONObject();
		        
				allItem.put("1", jsonObject);
				allItem.put("currentkey", 1);
				bw.write(allItem.toString()); //写入文件
	            bw.flush();    //刷新该流的缓冲
	            bw.close();
	            fw.close();
			}catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	public Vector<Map> getItemList(String path){
		Vector<Map> itemList = new Vector<Map>();
		
		File f = new File(path + File.separator +"store.txt");
		//System.out.println("where is it? :"+ path  + File.separator +  "store.txt");//在屏幕上输出
		if(f.exists()){
			try {
	            FileReader fr = new FileReader(path + File.separator + "store.txt");
	            BufferedReader br = new BufferedReader(fr);  
	            String myreadline;

                myreadline = br.readLine();//读取一行
                //System.out.println(myreadline);//在屏幕上输出
                JSONObject jsonObject = JSONObject.fromObject( myreadline );

                for(Integer i=Integer.parseInt(jsonObject.get("currentkey").toString());i>0;i--){
                	JSONObject itemI = JSONObject.fromObject(jsonObject.get(i.toString()));
                	
                	Map<Object, Object> contentList = new HashMap<Object, Object>();
                	JSONObject fieldsItem = JSONObject.fromObject(itemI.get("fields"));
                	JSONObject projectItem = JSONObject.fromObject(fieldsItem.get("project"));
                	
                	//System.out.println("itemI:"+i+" ---: " + fieldsItem.get("project").toString());
                	JSONObject issuetypeItem = JSONObject.fromObject(fieldsItem.get("issuetype"));
                	contentList.put("project",projectItem.get("key"));
                	contentList.put("summary",fieldsItem.get("summary"));
                	
                	contentList.put("issuetype",issuetypeItem.get("name"));
                	contentList.put("description",fieldsItem.get("description"));
                	
                	contentList.put("issuekey","");
                	contentList.put("issuelink","");
                	contentList.put("jirasite","");
                	contentList.put("jiraissueinfo","");
                	contentList.put("jiraissuewarn","");
                	try{
                		contentList.put("issuekey",itemI.get("issuekey"));
                		contentList.put("issuelink",itemI.get("issuelink"));
                		contentList.put("jirasite",itemI.get("jirasite"));
                		Map<Object, Object> issueInfoList = getIssueInfo(itemI.get("jirasite").toString(),itemI.get("issuekey").toString(),itemI.get("username").toString(),itemI.get("password").toString());
                		contentList.put("jiraissueinfo", issueInfoList);
                		
                	}catch(Exception e){
                		e.printStackTrace();
                    	contentList.put("jiraissuewarn","无法获取JIRA信息");
                    	System.out.println("itemI:获取JIRA连接失败");
                	}

                	itemList.addElement(contentList);
                }
                
	            br.close();
	            fr.close();
         
			}			        
			catch (IOException e) {
	            e.printStackTrace();
	        }
			
		}
		return itemList;
		
	}
	
}
