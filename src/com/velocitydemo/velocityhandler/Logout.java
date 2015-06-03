package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class Logout extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private static String PROPERTYNAME = StaticConstantVar.LDAP_PROPERTYNAME;
	private static String JIRA_PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();
	private static HashMap<String, String> jira_properties = new HashMap<String, String>();
	private LDAPAuthentication ldapc;
	private String HelpDeskPath = StaticConstantVar.HelpDeskPath;
	private String loginPath = StaticConstantVar.loginPath;

	private IssueInfo issueInfo = new IssueInfo();
	private String jirasiteUrl;
	private String Project;
	private String adminUsername;
	private String adminPassword;

	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		path = this.getServletContext().getRealPath("/");
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path
				+ StaticConstantVar.tempPath);
		prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "GBK");

		try {
			velo.init(prop);// 初始化设置，下面用到getTemplate("*.vm")输出时;一定要调用velo对象去做,即velo.getTemplate("*.vm")
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			if (properties.isEmpty()) {
				properties = CommonUtil.readFile(path + PROPERTYNAME);
			}
			ldapc = new LDAPAuthentication(properties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (jira_properties.isEmpty()) {
				jira_properties = CommonUtil.readFile(path + JIRA_PROPERTYNAME);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!jira_properties.isEmpty()) {
			this.jirasiteUrl = jira_properties.get("Protocol") + "://"
					+ jira_properties.get("URL") + ":"
					+ jira_properties.get("Port") + "/";
			issueInfo.setJirasite(jirasiteUrl);
			this.adminUsername = jira_properties.get("Username");
			this.adminPassword = jira_properties.get("Password");
		}

	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {

		if (IsLoggedIn.checkLogin(this, response, request)) {
			IsLoggedIn.logOutUser(response, request);
		}
		
		try {
			response.sendRedirect(loginPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setContentType("text/html; charset=gb2312");
		Template template = new Template();
		try {
			template = velo.getTemplate("login.vm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}

}
