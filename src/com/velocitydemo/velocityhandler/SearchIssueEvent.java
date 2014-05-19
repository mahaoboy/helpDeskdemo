package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class SearchIssueEvent extends VelocityViewServlet{
	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private String Project;
	private String Username;
	private String Password;

	private String PROPERTYNAME = "WEB-INF\\jira.conf";
	private static HashMap<String, String> properties = new HashMap<String, String>();

	private String loginPath = "Login";
	private String searchPath = "SearchIssueEvent";

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
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
			this.jirasiteUrl = properties.get("Protocol") + "://" +properties.get("URL") + ":"
					+ properties.get("Port") + "/";
			this.Project = properties.get("Project");
			this.Username = properties.get("Username");
			this.Password = properties.get("Password");
			issueInfo.setAduserName(properties.get("姓名"));
			issueInfo.setDepartmentName(properties.get("部门"));
			issueInfo.setInformationName(properties.get("联系电话邮件地址"));
		}
	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod();
		String aduname = "xx";
		/*
		 * if (IsLoggedIn.checkLogin(response, request)) { uname =
		 * IsLoggedIn.getUser(response, request); } else { try {
		 * response.sendRedirect(loginPath); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */
		ctx.put("uname", aduname);
		ctx.put("meth", meth);
		// System.out.println("Method:" + meth);
		if (meth == "POST") {


		}
		ctx.put("Createdissuekey", request.getAttribute("Createdissuekey"));
		// read file store
		Vector itemlist = issueInfo.getItemList(velo.getProperty(
				Velocity.FILE_RESOURCE_LOADER_PATH).toString());
		ctx.put("itemlist", itemlist);

		response.setContentType("text/html; charset=gb2312");
		Template template = new Template();
		try {
			template = velo.getTemplate("SearchIssueEvent.vm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return template;
	}
}
