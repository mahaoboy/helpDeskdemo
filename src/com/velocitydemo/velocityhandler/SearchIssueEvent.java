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
	private String deFaultStatusForSearch;
	private static String userName = "userName";
	private static String userPassword = "userPassword";
	
	private static String EMPTY_VALUE = "";

	private String[] checkPat = { "=", "\"", "'", "\\\\", "/" };
	private String checkForDate = "^\\d{4}-\\d{2}-\\d{2}$";

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
			this.Project = properties.get("Project");
			//this.Username = properties.get("Username");
			//this.Password = properties.get("Password");
			this.deFaultStatusForSearch = properties.get("默认搜索事件状态");
			issueInfo.setAduserName(properties.get("姓名"));
			issueInfo.setDepartmentName(properties.get("部门"));
			issueInfo.setInformationName(properties.get("联系电话邮件地址"));
			issueInfo.setResolutionDetailName(properties.get("解决方案"));
			issueInfo.setJirasite(jirasiteUrl);
			issueInfo.setProject(Project);
			issueInfo.setCloseAction(properties.get("关闭状态"));
			issueInfo.setReopenAction("重开动作");
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
		String adpassword = null;

		if (IsLoggedIn.checkLogin(this, response, request)) {
			aduname = IsLoggedIn.getUserInfo(this, response, request, userName);
			adpassword = IsLoggedIn.getUserInfo(this, response, request, userPassword);
			ctx.put("aduname", aduname);
			

			issueInfo.setUsername(aduname);
			issueInfo.setPassword(adpassword);
		} else {
			try {
				response.sendRedirect(loginPath);
				Template nulltemplate = new Template();
				try {
					nulltemplate = velo.getTemplate("SearchIssueEvent.vm");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return nulltemplate;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ctx.put("meth", meth);
		// System.out.println("Method:" + meth);

		if (meth.equals("POST") && request.getAttribute("Createdissuekey") == null) {
			String checkString = "";

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
			String createtimeend = request.getParameter("createtimeend")
					.isEmpty() ? "" : request.getParameter("createtimeend");
			String status = request.getParameter("status").isEmpty() ? ""
					: request.getParameter("status");
			String resolution = request.getParameter("resolution").isEmpty() ? ""
					: request.getParameter("resolution");
			String resolutiondescription = request.getParameter(
					"resolutiondescription").isEmpty() ? "" : request
					.getParameter("resolutiondescription");

			checkString += resolutiondescription + resolution + status
					+ createtime + information + department + issuetype
					+ description + summary;

			if ((!createtime.isEmpty() && !CommonUtil.checkStringValidat(
					createtime, checkForDate))
					|| (!createtimeend.isEmpty() && !CommonUtil
							.checkStringValidat(createtimeend, checkForDate))) {
				ctx.put("warn", "日期格式不对: yyyy-mm-dd");
			}
			else  if (!CommonUtil.checkStringValidation(checkString, checkPat)){
				ctx.put("warn",
						"输入字符不能包含以下字符：" + StringUtils.join(checkPat, " "));
			}else {
				try {
					if (aduname.equals(null)) {
						try {
							response.sendRedirect(loginPath);
							Template nulltemplate = new Template();
							try {
								nulltemplate = velo.getTemplate("SearchIssueEvent.vm");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return nulltemplate;
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Vector<Object> searchResultViaJQL = issueInfo
								.searchIssue(aduname, summary, description,
										issuetype, department, information,
										createtime, createtimeend, status,
										resolution, resolutiondescription);
						ctx.put("searchResultViaJQL", searchResultViaJQL);
						ctx.put("total", issueInfo.getTotalNumber());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 

			ctx.put("summary", summary);
			ctx.put("description", description);
			ctx.put("issuetype", issuetype);
			ctx.put("department", department);
			ctx.put("information", information);
			ctx.put("createtime", createtime);
			ctx.put("createtimeend", createtimeend);
			ctx.put("status", status);
			ctx.put("resolution", resolution);
			ctx.put("resolutiondescription", resolutiondescription);
		} else if (meth.equals("GET")) {
			try {
				if (aduname.equals(null)) {
					try {
						response.sendRedirect(loginPath);
						Template nulltemplate = new Template();
						try {
							nulltemplate = velo.getTemplate("SearchIssueEvent.vm");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return nulltemplate;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Vector<Object> searchResultViaJQL = issueInfo.searchIssue(
							aduname, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
							EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
							this.deFaultStatusForSearch, EMPTY_VALUE, EMPTY_VALUE);
					ctx.put("searchResultViaJQL", searchResultViaJQL);
					ctx.put("total", issueInfo.getTotalNumber());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				if (aduname.equals(null)) {
					try {
						response.sendRedirect(loginPath);
						Template nulltemplate = new Template();
						try {
							nulltemplate = velo.getTemplate("SearchIssueEvent.vm");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return nulltemplate;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Vector<Object> searchResultViaJQL = issueInfo.searchIssue(
							aduname, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
							EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
							this.deFaultStatusForSearch, EMPTY_VALUE, EMPTY_VALUE);
					ctx.put("searchResultViaJQL", searchResultViaJQL);
					ctx.put("total", issueInfo.getTotalNumber());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
