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

public class GoJIRA extends VelocityViewServlet {
	private String jirasiteUrl;
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jiraPath = StaticConstantVar.jiraLoginPath;
	private String os_username = StaticConstantVar.os_username;
	private String os_password = StaticConstantVar.os_password;
	private String os_destination = StaticConstantVar.os_destination;
	private String destinationPath = StaticConstantVar.destinationPathDashboard;
	private String loginPath = StaticConstantVar.loginPath;
	private HashMap<String, String> properties = new HashMap<String, String>();
	private static String userDisplayName = StaticConstantVar.userDisplayName;

	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;
	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static String userJIRApass = StaticConstantVar.userJIRApass;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!properties.isEmpty()) {
			jirasiteUrl = properties.get("Protocol") + "://"
					+ properties.get("URL") + ":" + properties.get("Port")
					+ "/";
		}

	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod();
		String uname = "";
		String upassword = "";
		Template nulltemplate = new Template();
		try {
			nulltemplate = velo.getTemplate("HelpDesk.vm");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (IsLoggedIn.checkLogin(this, response, request)) {
			uname = IsLoggedIn.getUserInfo(this, response, request, userDisplayName);
			upassword = IsLoggedIn.getUserInfo(this, response, request,
					userJIRApass);
			
			String destinationPathString = request.getParameterMap().containsKey("destinationPath") ? request.getParameter("destinationPath")
					: "";
			
			try {
				if (!jirasiteUrl.isEmpty()) {
					String redirectUrl = jirasiteUrl + jiraPath + "?"
							+ os_username + "=" + uname + "&" + os_password
							+ "=" + upassword + "&" + os_destination + "="
							+ (destinationPathString.isEmpty()?destinationPath:destinationPathString);
					System.out.println(redirectUrl + "   ccecececececececececexxxx");
					response.sendRedirect(redirectUrl);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				response.sendRedirect(loginPath);
				try {
					nulltemplate = velo.getTemplate("HelpDesk.vm");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nulltemplate;

	}
}
