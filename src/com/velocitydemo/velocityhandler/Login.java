package com.velocitydemo.velocityhandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class Login extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private static String PROPERTYNAME = StaticConstantVar.LDAP_PROPERTYNAME;
	private static String JIRA_PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();
	private static HashMap<String, String> jira_properties = new HashMap<String, String>();
	private LDAPAuthentication ldapc;
	private String HelpDeskPath = StaticConstantVar.HelpDeskPath;
	
	private IssueInfo issueInfo = new IssueInfo();
	private String jirasiteUrl;
	private String Project;
	private String adminUsername;
	private String adminPassword;
	
	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		path = this.getServletContext().getRealPath("/");
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + StaticConstantVar.tempPath);
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
					+ jira_properties.get("URL") + ":" + jira_properties.get("Port")
					+ "/";
			issueInfo.setJirasite(jirasiteUrl);
			this.adminUsername = jira_properties.get("Username");
			this.adminPassword = jira_properties.get("Password");
		}

	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod(); 
		
		if (IsLoggedIn.checkLogin(this, response, request)) {
			try {
				response.sendRedirect(HelpDeskPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (meth.equals("POST")) {
			String inusername = request.getParameter("username").isEmpty() ? ""
					: request.getParameter("username");
			String inpassword = request.getParameter("password").isEmpty() ? ""
					: request.getParameter("password");
			if (inusername.isEmpty() || inpassword.isEmpty()) {
				String warn = "用户名或密码不能为空";
				ctx.put("meth", warn);
			} else {
				if (ldapc.authenricate(inusername, inpassword)) {
					String displayNameofUser = ldapc.getUserDisplayName(inusername);
					String userMailAddress = ldapc.getUserAttribute(inusername, ldapc.userMailAddress);
					if(issueInfo.checkUserExistedOrNot(inusername, inpassword)){
						//IsLoggedIn.setLogin(response, request, inusername, inpassword, displayNameofUser, userMailAddress);
						IsLoggedIn.setLogin(response, request, adminUsername, adminPassword, inusername, userMailAddress, inpassword);
						
						ctx.put("meth", inusername);
						try {
							response.sendRedirect(HelpDeskPath);
							Template nulltemplate = new Template();
							try {
								nulltemplate = velo.getTemplate("login.vm");
							} catch (Exception e) {
								e.printStackTrace();
							}
							return nulltemplate;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						String warn = "无权限,JIRA中不存在此用户或密码不符合";
						ctx.put("meth", warn);
					}
				} else {
					String warn = "登陆失败,用户名或密码错误";
					ctx.put("meth", warn);
				}
			}
		} else if (meth.equals("GET")) {
			ctx.put("meth", "");
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