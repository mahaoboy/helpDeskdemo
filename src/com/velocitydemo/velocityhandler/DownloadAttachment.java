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

public class DownloadAttachment extends VelocityViewServlet {
	private String jirasiteUrl;
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jiraPath = "login.jsp";
	private String os_username = "os_username";
	private String os_password = "os_password";
	private String os_destination = "os_destination";
	private String destinationPath = "/secure/attachment/";
	private String loginPath = "Login";
	private String searchPath = "SearchIssueEvent";
	private HashMap<String, String> properties = new HashMap<String, String>();

	private static String userName = "userName";
	private static String userPassword = "userPassword";
	private String PROPERTYNAME = "WEB-INF\\jira.conf";

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + "temp");
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
			uname = IsLoggedIn.getUserInfo(this, response, request, userName);
			upassword = IsLoggedIn.getUserInfo(this, response, request,
					userPassword);
			try {
				String attid = request.getParameter("id").isEmpty() ? ""
						: request.getParameter("id");
				String attname = request.getParameter("name").isEmpty() ? ""
						: request.getParameter("name");
				if (attid.isEmpty() || attname.isEmpty() || jirasiteUrl.isEmpty()) {
						response.sendRedirect(searchPath);
				} else {
					response.sendRedirect(jirasiteUrl + jiraPath + "?"
							+ os_username + "=" + uname + "&" + os_password
							+ "=" + upassword + "&" + os_destination + "="
							+ destinationPath +attid+ "/"+attname);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					response.sendRedirect(searchPath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			try {
				response.sendRedirect(loginPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nulltemplate;

	}
}
