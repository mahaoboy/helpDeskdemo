package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class SearchIssueEvent extends VelocityViewServlet {
	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private String Project;
	private String Username;
	private String Password;
	
	private String[] checkPat = {"=","\"","'", "\\\\", "/"};

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
			this.jirasiteUrl = properties.get("Protocol") + "://"
					+ properties.get("URL") + ":" + properties.get("Port")
					+ "/";
			this.Project = properties.get("Project");
			this.Username = properties.get("Username");
			this.Password = properties.get("Password");
			issueInfo.setAduserName(properties.get("姓名"));
			issueInfo.setDepartmentName(properties.get("部门"));
			issueInfo.setInformationName(properties.get("联系电话邮件地址"));
			issueInfo.setResolutionDetailName(properties.get("解决方案"));
			issueInfo.setJirasite(jirasiteUrl);
			issueInfo.setProject(Project);
			issueInfo.setUsername(Username);
			issueInfo.setPassword(Password);
		}
	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String meth = request.getMethod();
		String aduname = null;

		if (IsLoggedIn.checkLogin(this, response, request)) {
			aduname = IsLoggedIn.getUser(this, response, request);
		} else {
			try {
				response.sendRedirect(loginPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ctx.put("aduname", aduname);
		ctx.put("meth", meth);
		// System.out.println("Method:" + meth);

		if (meth == "POST" && request.getAttribute("Createdissuekey") == null) {
			String checkString = "";
			String uname = request.getParameter("uname").isEmpty() ? ""
					: request.getParameter("uname");
			String summary = request.getParameter("summary").isEmpty() ? ""
					: request.getParameter("summary");
			String description = request.getParameter("description").isEmpty() ? ""
					: request.getParameter("description");
			String issuetype = request.getParameter("issuetype").isEmpty() ? ""
					: request.getParameter("issuetype");
			String department = request.getParameter("department").isEmpty() ? ""
					: request.getParameter("department");
			String information = request.getParameter("information").isEmpty() ? ""
					: request.getParameter("information");

			String createtime = request.getParameter("createtime").isEmpty() ? ""
					: request.getParameter("createtime");
			String status = request.getParameter("status").isEmpty() ? ""
					: request.getParameter("status");
			String resolution = request.getParameter("resolution").isEmpty() ? ""
					: request.getParameter("resolution");
			String resolutiondescription = request.getParameter(
					"resolutiondescription").isEmpty() ? "" : request
					.getParameter("resolutiondescription");

			checkString += uname + resolutiondescription + resolution + status
					+ createtime + information + department + issuetype
					+ description + summary;

			if(CommonUtil.checkStringValidation(checkString, checkPat)){
				try {
					Vector<Object> searchResultViaJQL = issueInfo.searchIssue(uname, summary, description, issuetype,
							department, information, createtime, status,
							resolution, resolutiondescription);
					ctx.put("searchResultViaJQL", searchResultViaJQL);
					ctx.put("total", issueInfo.getTotalNumber());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				ctx.put("warn", "输入字符不能包含以下字符：" + StringUtils.join(checkPat, " "));
			}
			
			
			ctx.put("uname", uname);
			ctx.put("summary", summary);
			ctx.put("description", description);
			ctx.put("issuetype", issuetype);
			ctx.put("department", department);
			ctx.put("information", information);
			ctx.put("createtime", createtime);
			ctx.put("status", status);
			ctx.put("resolution", resolution);
			ctx.put("resolutiondescription", resolutiondescription);
		}

		Vector issuetypes = new Vector();
		Vector statuses = new Vector();
		Vector resolutions = new Vector();
		try {
			issuetypes = issueInfo.getIssueTypes();
			ctx.put("issuetypes", issuetypes);

			statuses = issueInfo.getIssueStatus();
			ctx.put("statuss", statuses);

			resolutions = issueInfo.getIssueResolution();
			ctx.put("resolutions", resolutions);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ctx.put("Createdissuekey", request.getAttribute("Createdissuekey"));
		// read file store

		response.setContentType("text/html; charset=utf-8");
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
