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
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class CreateIssueEvent extends VelocityViewServlet {
	private static Logger logger = Logger.getLogger(CreateIssueEvent.class);
	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private String Project;
	private String Username;
	private String Password;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;
	private static String userDisplayName = StaticConstantVar.userDisplayName;
	private static String userMailAdd = StaticConstantVar.userMailAdd;
	private String[] checkPat = StaticConstantVar.checkPat;
	private String[] numberCheckPat = StaticConstantVar.numberPat;

	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();

	private String loginPath = StaticConstantVar.loginPath;
	private String searchPath = StaticConstantVar.searchPath;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path
				+ StaticConstantVar.tempPath);
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
			// this.Username = properties.get("Username");
			// this.Password = properties.get("Password");
			issueInfo.setDepartmentName(properties.get("申请部门"));
			//issueInfo.setInformationName(properties.get("联系方式"));
			issueInfo.setEventType(properties.get("桌面请求类型"));
			issueInfo.setSpecificDescription(properties.get("描述"));
			issueInfo.setRank(properties.get("职级"));
			issueInfo.setFee(properties.get("费用"));
			issueInfo.setIssuetype(properties.get("issueType"));
			issueInfo.setJirasite(jirasiteUrl);
			issueInfo.setProject(Project);
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
		String displayName = null;
		String mailAdd = null; 

		if (IsLoggedIn.checkLogin(this, response, request)) {
			aduname = IsLoggedIn.getUserInfo(this, response, request, userName);
			adpassword = IsLoggedIn.getUserInfo(this, response, request,
					userPassword);
			displayName = IsLoggedIn.getUserInfo(this, response, request,
					userDisplayName);
			mailAdd = IsLoggedIn.getUserInfo(this, response, request,
					userMailAdd);
			logger.debug(mailAdd + "xxxxxxxxxxx");
			ctx.put("aduname", displayName);

			issueInfo.setUsername(aduname);
			issueInfo.setPassword(adpassword);
		} else {
			try {
				response.sendRedirect(loginPath);
				Template nulltemplate = new Template();
				try {
					nulltemplate = velo.getTemplate("CreateIssueEvent.vm");
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
		if (meth == "POST") {

			String summary = request.getParameter("summary").isEmpty() ? ""
					: request.getParameter("summary");
			String description = request.getParameter("description").isEmpty() ? ""
					: request.getParameter("description");
			String issuetype = request.getParameter("issuetype").isEmpty() ? ""
					: request.getParameter("issuetype");
			String department = request.getParameter("department").isEmpty() ? ""
					: request.getParameter("department");
			String information = mailAdd;
			String rankString = request.getParameterMap().containsKey("rank") ?  request.getParameter("rank") : "" ;
			String feeString = request.getParameterMap().containsKey("fee") ?  request.getParameter("fee") : "" ;
			String checkString = information + department + issuetype
					+ description + summary + rankString + feeString;

			// Map<String, Object> infoList = new HashMap<String, Object>();
			if (jirasiteUrl.isEmpty() || Project.isEmpty() || summary.isEmpty()
					|| description.isEmpty() || issuetype.isEmpty()
					|| information.isEmpty() || department.isEmpty() || feeString.isEmpty() || rankString.isEmpty()) {
				String warn = "创建失败，字段不能为空";
				ctx.put("warn", warn);
			} else if (!CommonUtil.checkStringValidation(checkString, checkPat)) {
				ctx.put("warn",
						"输入字符不能包含以下字符：" + StringUtils.join(checkPat, " "));
			}else if (!CommonUtil.checkStringValidation(feeString,
					numberCheckPat)) {
				ctx.put("warn",
						"费用字段输入字符只能是数字");
			} else {
				String responseStr = "";
				try {
					responseStr = issueInfo.createIssue(jirasiteUrl, Project,
							summary, description, issuetype, aduname,
							adpassword, information, department, displayName, feeString, rankString);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (responseStr.isEmpty()) {
					String warn = "创建事件失败";
					ctx.put("warn", warn);

				} else {
					request.setAttribute("Createdissuekey", responseStr);
					request.setAttribute("Createdissuelink",
							issueInfo.getIssueLink());

					request.setAttribute("", "GET");
					RequestDispatcher rd = null;
					rd = this.getServletContext().getRequestDispatcher(
							searchPath);
					try {
						rd.forward(request, response);
					} catch (ServletException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						String warn = "创建事件成功， " + responseStr;
						ctx.put("warn", warn);
					}
				}
			}

			ctx.put("summary", summary);
			ctx.put("description", description);
			ctx.put("department", department);
			ctx.put("issuetype", issuetype);
		}
		// read file store
		Vector itemlist;
		Vector departMent;
		try {
			itemlist = issueInfo.getEventTypes();
			departMent = issueInfo.getCreateMeta(issueInfo.getDepartmentName());
			ctx.put("itemlist", itemlist);
			ctx.put("departmentitem", departMent);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			String warn = "从JIRA获取信息失败";
			ctx.put("warn", warn);
		}

		response.setContentType("text/html; charset=utf-8");
		Template template = new Template();
		try {
			template = velo.getTemplate("CreateIssueEvent.vm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return template;
	}
}
