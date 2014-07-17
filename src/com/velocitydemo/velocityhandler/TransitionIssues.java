package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class TransitionIssues extends HttpServlet {

	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private String Project;
	private String Username;
	private String Password;
	private String deFaultStatusForSearch;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;

	private static String EMPTY_VALUE = StaticConstantVar.EMPTY_VALUE;

	private String[] checkPat = StaticConstantVar.checkPat;
	private String checkForDate = StaticConstantVar.checkForDate;

	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();

	private String loginPath = StaticConstantVar.loginPath;
	private String searchPath = StaticConstantVar.searchPath;

	@Override
	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + StaticConstantVar.tempPath);
		prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");

		try {
			velo.init(prop);// 初始化设置，下面用到getTemplate("*.vm")输出时;一定要调用velo对象去做,即velo.getTemplate("*.vm")
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			if (properties.isEmpty()) {
				properties = CommonUtil.readFile(path + PROPERTYNAME);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!properties.isEmpty()) {
			this.jirasiteUrl = properties.get("Protocol") + "://"
					+ properties.get("URL") + ":" + properties.get("Port")
					+ "/";
			issueInfo.setJirasite(jirasiteUrl);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (IsLoggedIn.checkLogin(this, response, request)) {
			String uname = IsLoggedIn.getUserInfo(this, response, request,
					userName);
			String upassword = IsLoggedIn.getUserInfo(this, response, request,
					userPassword);
			issueInfo.setUsername(uname);
			issueInfo.setPassword(upassword);
			
			String issueKey = request.getParameter("issueKey").isEmpty() ? ""
					: request.getParameter("issueKey");
			String transitionId = request.getParameter("transitionId")
					.isEmpty() ? "" : request.getParameter("transitionId");
			

			response.setCharacterEncoding("UTF-8");
			
			if (!issueKey.isEmpty() && !transitionId.isEmpty()) {
				
				JSONObject transitionInfoJson = new JSONObject();
				JSONObject itemContent = new JSONObject();
				
				try {
					if (issueInfo.transitionIssue(issueKey, transitionId)) {
						itemContent.put("result", "sucess");
						itemContent.put("issuekey", issueKey);
						HashMap<Object, Object> issueInfoItem = (HashMap<Object, Object>) issueInfo.getIssueInfo(issueInfo.getJirasite(),  issueKey,
								issueInfo.getUsername(), issueInfo.getPassword());
						itemContent.put("status", issueInfoItem.get("status"));
					}else{
						itemContent.put("result", "failed");
					}
					transitionInfoJson.put("content", itemContent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				response.setContentType("application/json");
				response.getWriter().write(transitionInfoJson.toString());
			} else {
				response.getWriter().write("");
			}

		} else {
			response.getWriter().write("loginNeeded");
		}
	}
}
