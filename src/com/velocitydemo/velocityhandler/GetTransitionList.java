package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class GetTransitionList extends HttpServlet {

	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;

	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();

	@Override
	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path +  StaticConstantVar.tempPath);
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
			issueInfo.setCloseAction(properties.get("关闭动作"));
			issueInfo.setReopenAction(properties.get("重开动作"));
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (IsLoggedIn.checkLogin(this, response, request)) {
			String uname = IsLoggedIn.getUserInfo(this, response, request,
					userName);
			String upassword = IsLoggedIn.getUserInfo(this, response, request,
					userPassword);


			issueInfo.setUsername(uname);
			issueInfo.setPassword(upassword);
			
			String issueKey = request.getParameter("issueKey").isEmpty() ? ""
					: request.getParameter("issueKey");

			if (!issueKey.isEmpty()) {

				issueInfo.getTransitionList(issueKey);

				JSONObject transitionInfo = new JSONObject();
				JSONObject closeInfoList = new JSONObject();
				JSONObject reopenInfoList = new JSONObject();

				if (!issueInfo.getCloseActionDetail().equals(null)) {
					closeInfoList.put("id", issueInfo.getCloseActionDetail()
							.get("id"));
					closeInfoList.put("name", issueInfo.getCloseActionDetail()
							.get("name"));
					transitionInfo.put("closeInfoList", closeInfoList);
				}

				if (!issueInfo.getReopenActionDetail().equals(null)) {
					reopenInfoList.put("id", issueInfo.getReopenActionDetail()
							.get("id"));
					reopenInfoList.put("name", issueInfo
							.getReopenActionDetail().get("name"));
					transitionInfo.put("reopenInfoList", reopenInfoList);
				}

				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(transitionInfo.toString());
			} else {
				response.getWriter().write("");
			}

		} else {
			response.getWriter().write("loginNeeded");
		}
	}

}
