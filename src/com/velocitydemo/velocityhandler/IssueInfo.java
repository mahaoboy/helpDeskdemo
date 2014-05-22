package com.velocitydemo.velocityhandler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getAduser() {
		return aduser;
	}

	public void setAduser(String aduser) {
		this.aduser = aduser;
	}

	private String department;
	private String information;
	private String aduser;

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getInformationName() {
		return informationName;
	}

	public void setInformationName(String informationName) {
		this.informationName = informationName;
	}

	public String getAduserName() {
		return aduserName;
	}

	public void setAduserName(String aduserName) {
		this.aduserName = aduserName;
	}

	private String departmentName;
	private String informationName;
	private String aduserName;
	private String resolutionDetail;
	private String resolutionDetailName;
	private String totalNumber;

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getResolutionDetailName() {
		return resolutionDetailName;
	}

	public void setResolutionDetailName(String resolutionDetailName) {
		this.resolutionDetailName = resolutionDetailName;
	}

	public String getResolutionDetail() {
		return resolutionDetail;
	}

	public void setResolutionDetail(String resolutionDetail) {
		this.resolutionDetail = resolutionDetail;
	}

	private String createJson() {
		JSONObject outJson = new JSONObject();
		JSONObject fieldsj = new JSONObject();
		JSONObject projectj = new JSONObject();
		JSONObject issuetypej = new JSONObject();

		issuetypej.put("name", issuetype);
		projectj.put("key", project);

		fieldsj.put("summary", summary);
		fieldsj.put("issuetype", issuetypej);
		fieldsj.put("project", projectj);
		fieldsj.put("description", description);
		if (this.aduserName != null) {
			fieldsj.put(this.aduserName, this.aduser);
			fieldsj.put(this.departmentName, this.department);
			fieldsj.put(this.informationName, this.information);
		}
		outJson.put("fields", fieldsj);

		return outJson.toString();
	}

	private String getJson(String jsonStr) {
		String key = getKey(jsonStr);
		issueLink = getSiteLink("browse/" + key);
		return issueLink;
	}

	private String getKey(String response) {
		JSONObject jsonObject = JSONObject.fromObject(response);

		issueKey = jsonObject.get("key").toString();
		return issueKey;
	}

	private void addKey() {
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);

		jsonObject.put("issuekey", issueKey);
		jsonObject.put("jirasite", getSiteLink(""));
		jsonObject.put("issuelink", issueLink);
		jsonObject.put("username", username);
		jsonObject.put("password", password);
		jsonStr = jsonObject.toString();
	}

	public String createIssue(String jirasite, String project, String summary,
			String description, String issuetype, String username,
			String password) throws Exception {

		this.jirasite = jirasite;
		this.project = project;
		this.summary = summary;
		this.description = description;
		this.issuetype = issuetype;
		this.username = username;
		this.password = password;

		jsonStr = createJson();
		System.out.println("jsonStr:---: " + jsonStr);

		return sendPost();
	}

	public String createIssue(String jirasite, String project, String summary,
			String description, String issuetype, String username,
			String password, String information, String department,
			String aduname) throws Exception {

		this.jirasite = jirasite;
		this.project = project;
		this.summary = summary;
		this.description = description;
		this.issuetype = issuetype;
		this.username = username;
		this.password = password;
		this.information = information;
		this.department = department;
		this.aduser = aduname;

		jsonStr = createJson();
		System.out.println("jsonStr:---: " + jsonStr);

		return sendPost();
	}

	public void createIssue(String project, String summary, String description,
			String issuetype, String username) {

		this.project = project;
		this.summary = summary;
		this.description = description;
		this.issuetype = issuetype;
		this.username = username;
		System.out.println("jsonStr:---: ");
		jsonStr = createJson();
		System.out.println("jsonStr:---: " + jsonStr);

	}

	private String getSiteLink(String extraLink) {
		Pattern pattern = Pattern.compile("/");
		String lastL = jirasite.substring(jirasite.length() - 1);
		Matcher matcher = pattern.matcher(lastL);
		boolean urlCheck = matcher.matches();
		String url = "";
		// System.out.println("Test Last Letter: ----- : "+ lastL);
		if (urlCheck) {
			url = jirasite + extraLink;
			// System.out.println("Test url: ----- : "+ url);
		} else {
			url = jirasite + "/" + extraLink;
			// System.out.println("Test1 url: ----- : "+ url);
		}
		return url;
	}

	private Map<Object, Object> getIssueInfo(String jirasite, String issuekey,
			String usernameT, String passwordT) throws IOException {

		String url = jirasite + "rest/api/2/issue/" + issuekey;

		StringBuffer response = getConnectionToJira(url);

		// print result
		System.out.println("Response String : " + response.toString());
		JSONObject jsonObject = JSONObject.fromObject(response.toString());

		HashMap<Object, Object> issueinfo = new HashMap<Object, Object>();

		JSONObject fieldsJ = JSONObject.fromObject(jsonObject.get("fields"));

		JSONObject statusJ = JSONObject.fromObject(fieldsJ.get("status"));
		Object[] fixVersionsJ = JSONArray.fromObject(
				fieldsJ.get("fixVersions").toString()).toArray();
		JSONObject assigneeJ = JSONObject.fromObject(fieldsJ.get("assignee"));

		String statusNameJ = statusJ.get("name").toString();
		String displayNameJ = assigneeJ.get("displayName").toString();

		String fixversionS = "";
		JSONObject fixVersionsJI;
		for (int i = 0; i < fixVersionsJ.length; i++) {
			if (!fixversionS.isEmpty())
				fixversionS += ",";
			fixVersionsJI = JSONObject.fromObject(fixVersionsJ[i]);
			fixversionS += fixVersionsJI.get("name");
			System.out.println("\n fixversions: " + fixversionS);
		}

		issueinfo.put("assignee", displayNameJ);
		issueinfo.put("status", statusNameJ);
		issueinfo.put("fixVersions", fixversionS);
		return issueinfo;
	}

	private HttpURLConnection getPostConnectionToJira(String url)
			throws Exception {
		URL obj = new URL(url);
		// HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		// con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type",
				"application/json; charset=UTF-8");
		// con.setRequestProperty("data", jsonStr);

		BASE64Encoder base64Encoder = new BASE64Encoder();
		String authStr = username + ":" + password;
		String authEnc = base64Encoder.encode(authStr.getBytes());
		con.setRequestProperty("Authorization", "Basic " + authEnc);

		return con;
	}

	private String sendPost() throws Exception {

		String url = getSiteLink("rest/api/2/issue/");
		System.out.println(url);

		HttpURLConnection con = getPostConnectionToJira(url);

		String urlParameters = jsonStr;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedOutputStream bos = new BufferedOutputStream(wr);
		bos.write(urlParameters.getBytes("UTF-8"));
		bos.flush();
		bos.close();
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		StringBuffer response = new StringBuffer();
		String inputLine;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {

			e.printStackTrace();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}

		// print result
		System.out.println("Response String : " + response.toString());
		getKey(response.toString());
		getJson(response.toString());
		addKey();
		return issueKey;
	}

	public String getIssueLink() {
		return issueLink;
	}

	public void writeFile(String path) {
		File f = new File(path + File.separator + "store.txt");
		// System.out.println("where is it? :"+ path + "/store.txt");//在屏幕上输出
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(path + File.separator
						+ "store.txt");
				BufferedReader br = new BufferedReader(fr);
				String myreadline;

				myreadline = br.readLine();// 读取一行
				System.out.println(myreadline);// 在屏幕上输出

				br.close();
				fr.close();
				try {
					FileWriter fw = new FileWriter(path + File.separator
							+ "store.txt");
					BufferedWriter bw = new BufferedWriter(fw);
					JSONObject jsonObject = JSONObject.fromObject(myreadline);
					JSONObject jsonStrO = JSONObject.fromObject(jsonStr);

					Integer keyValue = Integer.parseInt(jsonObject.get(
							"currentkey").toString()) + 1;
					jsonObject.put(keyValue.toString(), jsonStrO);
					jsonObject.put("currentkey", keyValue);

					bw.write(jsonObject.toString()); // 写入文件
					bw.flush(); // 刷新该流的缓冲
					bw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			try {
				FileWriter fw = new FileWriter(path + File.separator
						+ "store.txt");
				BufferedWriter bw = new BufferedWriter(fw);
				JSONObject jsonObject = JSONObject.fromObject(jsonStr);
				JSONObject allItem = new JSONObject();

				allItem.put("1", jsonObject);
				allItem.put("currentkey", 1);
				bw.write(allItem.toString()); // 写入文件
				bw.flush(); // 刷新该流的缓冲
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Vector<Map> getItemList(String path) {
		Vector<Map> itemList = new Vector<Map>();

		File f = new File(path + File.separator + "store.txt");
		// System.out.println("where is it? :"+ path + File.separator +
		// "store.txt");//在屏幕上输出
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(path + File.separator
						+ "store.txt");
				BufferedReader br = new BufferedReader(fr);
				String myreadline;

				myreadline = br.readLine();// 读取一行
				// System.out.println(myreadline);//在屏幕上输出
				JSONObject jsonObject = JSONObject.fromObject(myreadline);

				for (Integer i = Integer.parseInt(jsonObject.get("currentkey")
						.toString()); i > 0; i--) {
					JSONObject itemI = JSONObject.fromObject(jsonObject.get(i
							.toString()));

					Map<Object, Object> contentList = new HashMap<Object, Object>();
					JSONObject fieldsItem = JSONObject.fromObject(itemI
							.get("fields"));
					JSONObject projectItem = JSONObject.fromObject(fieldsItem
							.get("project"));

					// System.out.println("itemI:"+i+" ---: " +
					// fieldsItem.get("project").toString());
					JSONObject issuetypeItem = JSONObject.fromObject(fieldsItem
							.get("issuetype"));
					contentList.put("project", projectItem.get("key"));
					contentList.put("summary", fieldsItem.get("summary"));

					contentList.put("issuetype", issuetypeItem.get("name"));
					contentList.put("description",
							fieldsItem.get("description"));

					contentList.put("issuekey", "");
					contentList.put("issuelink", "");
					contentList.put("jirasite", "");
					contentList.put("jiraissueinfo", "");
					contentList.put("jiraissuewarn", "");
					try {
						contentList.put("issuekey", itemI.get("issuekey"));
						contentList.put("issuelink", itemI.get("issuelink"));
						contentList.put("jirasite", itemI.get("jirasite"));
						Map<Object, Object> issueInfoList = getIssueInfo(itemI
								.get("jirasite").toString(),
								itemI.get("issuekey").toString(),
								itemI.get("username").toString(),
								itemI.get("password").toString());
						contentList.put("jiraissueinfo", issueInfoList);

					} catch (Exception e) {
						e.printStackTrace();
						contentList.put("jiraissuewarn", "无法获取JIRA信息");
						System.out.println("itemI:获取JIRA连接失败");
					}

					itemList.addElement(contentList);
				}

				br.close();
				fr.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return itemList;

	}

	private StringBuffer getConnectionToJira(String url) throws IOException {
		URL obj = new URL(url);
		// HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		BASE64Encoder base64Encoder = new BASE64Encoder();
		String authStr = username + ":" + password;
		String authEnc = base64Encoder.encode(authStr.getBytes());
		con.setRequestProperty("Authorization", "Basic " + authEnc);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		StringBuffer response = new StringBuffer();
		String inputLine;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {

			e.printStackTrace();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		return response;
	}

	@SuppressWarnings("null")
	public Vector<Object> getIssueTypes() throws IOException {

		String url = getSiteLink("rest/api/2/project/" + project);

		StringBuffer response = getConnectionToJira(url);

		System.out.println("Response String : " + response.toString());
		JSONObject jsonObject = JSONObject.fromObject(response.toString());

		Vector<Object> issueTypeN = new Vector<Object>();
		Object[] issueTypesJ = JSONArray.fromObject(
				jsonObject.get("issueTypes")).toArray();
		JSONObject issueTypeJI;
		for (int i = 0; i < issueTypesJ.length; i++) {

			issueTypeJI = JSONObject.fromObject(issueTypesJ[i]);
			String nameI = issueTypeJI.get("name").toString();
			Boolean subtaksOrNot = (Boolean) issueTypeJI.get("subtask");
			if (!nameI.isEmpty() && !subtaksOrNot) {
				issueTypeN.add(nameI);
			}
		}

		return issueTypeN;
	}

	public Vector<Object> getIssueStatus() throws IOException {

		String url = getSiteLink("rest/api/2/project/" + project + "/statuses");

		StringBuffer response = getConnectionToJira(url);

		// print result
		System.out.println("Response String : " + response.toString());

		Vector<Object> statusN = new Vector<Object>();
		Object[] statusJ = JSONArray.fromObject(response.toString()).toArray();
		JSONObject statusJI;
		for (int i = 0; i < statusJ.length; i++) {
			Object[] statusJJ = JSONArray.fromObject(
					JSONObject.fromObject(statusJ[i]).get("statuses"))
					.toArray();
			for (int j = 0; j < statusJJ.length; j++) {
				statusJI = JSONObject.fromObject(statusJJ[j]);
				String nameI = statusJI.get("name").toString();
				if (!statusN.contains(nameI)) {
					statusN.add(nameI);
				}
			}
		}
		return statusN;
	}

	public Vector<Object> getIssueResolution() throws IOException {

		String url = getSiteLink("rest/api/2/resolution");

		StringBuffer response = getConnectionToJira(url);

		// print result
		System.out.println("Response String : " + response.toString());

		Vector<Object> resolutionN = new Vector<Object>();
		Object[] resolutionJ = JSONArray.fromObject(response.toString())
				.toArray();
		JSONObject resolutionJI;
		for (int i = 0; i < resolutionJ.length; i++) {

			resolutionJI = JSONObject.fromObject(resolutionJ[i]);
			String nameI = resolutionJI.get("name").toString();
			if (!resolutionN.contains(nameI)) {
				resolutionN.add(nameI);
			}
		}
		return resolutionN;
	}

	private void getFieldName() throws IOException {
		String url = getSiteLink("rest/api/2/field");

		StringBuffer response = getConnectionToJira(url);

		System.out.println("Response String : " + response.toString());

		String fieldNameN = "";
		Object[] fieldNameJ = JSONArray.fromObject(response.toString())
				.toArray();
		JSONObject fieldNameJI;
		for (int i = 0; i < fieldNameJ.length; i++) {
			fieldNameJI = JSONObject.fromObject(fieldNameJ[i]);
			if (fieldNameJI.get("id").toString()
					.equals(this.resolutionDetailName)) {
				this.resolutionDetail = fieldNameJI.get("name").toString();
			} else if (fieldNameJI.get("id").toString()
					.equals(this.departmentName)) {
				this.department = fieldNameJI.get("name").toString();
			} else if (fieldNameJI.get("id").toString()
					.equals(this.informationName)) {
				this.information = fieldNameJI.get("name").toString();
			} else if (fieldNameJI.get("id").toString().equals(this.aduserName)) {
				this.aduser = fieldNameJI.get("name").toString();
			}
		}
	}

	private String createJqlJson(String uname, String summary,
			String description, String issuetype, String department,
			String information, String createtime, String status,
			String resolution, String resolutiondescription) {
		JSONObject outJson = new JSONObject();
		JSONArray fieldsj = new JSONArray();

		// project=TUTORIAL and summary ~ 'aa' and status = closed and
		// resolution = fixed and description ~ 'aa' and issuetype = Bug and
		// '部门' ~ 'aa' and '姓名' ~ 'aa' and '联系电话&邮件地址' ~ 'aa' and '解决方案' ~ 'aa'
		// and createdDate = 2013-04-22
		String jql = "project = " + this.project;

		if (!uname.isEmpty()) {
			jql += " and '" + this.aduser + "'" + " ~ " + "'" + uname + "'";
		}
		if (!summary.isEmpty()) {
			jql += " and summary ~ " + "'" + summary + "'";
		}
		if (!description.isEmpty()) {
			jql += " and description ~ " + "'" + description + "'";
		}
		if (!issuetype.isEmpty()) {
			jql += " and issuetype = " + issuetype;
		}
		if (!department.isEmpty()) {
			jql += " and '" + this.department + "'" + " ~ " + "'" + department
					+ "'";
		}
		if (!information.isEmpty()) {
			jql += " and '" + this.information + "'" + " ~ " + "'"
					+ information + "'";
		}
		if (!resolutiondescription.isEmpty()) {
			jql += " and '" + this.resolutionDetail + "'" + " ~ " + "'"
					+ resolutiondescription + "'";
		}
		if (!status.isEmpty()) {
			jql += " and status = " + status;
		}
		if (!resolution.isEmpty()) {
			jql += " and resolution = " + resolution;
		}

		if (!createtime.isEmpty()) {
			jql += " and createdDate = " + createtime;
		}

		outJson.put("jql", jql);

		fieldsj.add("summary");
		fieldsj.add("description");
		fieldsj.add("status");
		fieldsj.add("issuetype");
		fieldsj.add("created");
		fieldsj.add("resolution");
		fieldsj.add(this.aduserName);
		fieldsj.add(this.departmentName);
		fieldsj.add(this.informationName);
		fieldsj.add(this.resolutionDetailName);
		outJson.put("fields", fieldsj);

		return outJson.toString();
	}

	public Vector<Object> searchIssue(String uname, String summary,
			String description, String issuetype, String department,
			String information, String createtime, String status,
			String resolution, String resolutiondescription) throws Exception {

		String url = jirasite + "rest/api/2/search";

		HttpURLConnection con = getPostConnectionToJira(url);

		getFieldName();
		String urlParameters = createJqlJson(uname, summary, description,
				issuetype, department, information, createtime, status,
				resolution, resolutiondescription);

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		BufferedOutputStream bos = new BufferedOutputStream(wr);
		bos.write(urlParameters.getBytes("UTF-8"));
		bos.flush();
		bos.close();
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		StringBuffer response = new StringBuffer();
		String inputLine;

		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {

			e.printStackTrace();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}

		// print result
		System.out.println("Response String : " + response.toString());
		Vector<Object> searchResult = new Vector<Object>();
		JSONObject resultViaJQLJ = JSONObject.fromObject(response.toString());
		totalNumber = resultViaJQLJ.get("total").toString();
		Object[] issuesJ = JSONArray.fromObject(resultViaJQLJ.get("issues"))
				.toArray();
		JSONObject issueJI;
		for (int i = 0; i < issuesJ.length; i++) {
			HashMap<String, String> issueInfolist = new HashMap<String, String>();

			issueJI = JSONObject.fromObject(issuesJ[i]);
			JSONObject fields = JSONObject.fromObject(issueJI.get("fields"));
			if (!fields.isNullObject()) {
				issueInfolist.put(
						"summaryitem",
						fields.get("summary").equals(null) ? "" : fields.get(
								"summary").toString());
				issueInfolist.put("descriptionitem", fields.get("description")
						.equals(null) ? "" : fields.get("description")
						.toString());
				issueInfolist.put("statusitem",
						fields.get("status").equals(null) ? "" : JSONObject
								.fromObject(fields.get("status")).get("name")
								.toString());
				issueInfolist.put("issuetypeitem",
						fields.get("issuetype").equals(null) ? "" : JSONObject
								.fromObject(fields.get("issuetype"))
								.get("name").toString());
				issueInfolist.put(
						"createditem",
						fields.get("created").equals(null) ? "" : CommonUtil.formatDateFromString(fields.get(
								"created").toString(), "yyyy-MM-dd'T'hh:mm:ss", "yyyy-MM-dd hh:mm:ss"));
				issueInfolist.put(
						"resolutionitem",
						fields.get("resolution").equals(null) ? "" : JSONObject
								.fromObject(fields.get("resolution"))
								.get("name").toString());
				issueInfolist.put("unameitem", fields.get(this.aduserName)
						.equals(null) ? "" : fields.get(this.aduserName)
						.toString());
				issueInfolist.put("departmentitem",
						fields.get(this.departmentName).equals(null) ? ""
								: fields.get(this.departmentName).toString());
				issueInfolist.put("informationitem",
						fields.get(this.informationName).equals(null) ? ""
								: fields.get(this.informationName).toString());
				issueInfolist.put("resolutiondescriptionitem",

				fields.get(this.resolutionDetailName).equals(null) ? ""
						: fields.get(this.resolutionDetailName).toString());
				searchResult.add(issueInfolist);
			}
		}
		return searchResult;
	}
}
