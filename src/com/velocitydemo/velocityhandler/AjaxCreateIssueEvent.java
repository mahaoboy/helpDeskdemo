package com.velocitydemo.velocityhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class AjaxCreateIssueEvent extends VelocityViewServlet {
	private IssueInfo issueInfo = new IssueInfo();
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String jirasiteUrl;
	private String Project;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;
	private static String userDisplayName = StaticConstantVar.userDisplayName;
	private static String userMailAdd = StaticConstantVar.userMailAdd;
	private String[] checkPat = StaticConstantVar.checkPat;
	private String[] numberCheckPat = StaticConstantVar.numberPat;

	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private String PROPERTYNAME_FILE = StaticConstantVar.PROPERTYNAME_FILE_UPLOAD;
	private static String UPLOADFILE = StaticConstantVar.UPLOADFILE;
	private static String createPath = StaticConstantVar.createPath;

	private static HashMap<String, String> properties = new HashMap<String, String>();

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
			this.Project = properties.get("Project");
			// this.Username = properties.get("Username");
			// this.Password = properties.get("Password");
			issueInfo.setAduserName(properties.get("域用户姓名"));
			issueInfo.setUserInputName(properties.get("姓名"));
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

		Template nulltemplate = new Template();
		response.setContentType("text/html; charset=utf-8");
		try {
			nulltemplate = velo.getTemplate("AjaxCreateIssueEvent.vm");
			request.setCharacterEncoding("UTF-8");
		} catch (Exception e3) {
			e3.printStackTrace();
		}

		String meth = request.getMethod();
		String strContentType = request.getContentType();
		System.out.println(strContentType);

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
			ctx.put("aduname", displayName);

			issueInfo.setUsername(aduname);
			issueInfo.setPassword(adpassword);
		} else {
			ctx.put("warn", "login");
			return nulltemplate;
		}

		ctx.put("meth", meth);
		// System.out.println("Method:" + meth);
		if (meth.equals("POST")) {
			try {
				String summary = request.getParameter("summary").isEmpty() ? ""
						: request.getParameter("summary");
				String description = request.getParameter("description")
						.isEmpty() ? "" : request.getParameter("description");
				String issuetype = request.getParameter("issuetype").isEmpty() ? ""
						: request.getParameter("issuetype");
				String department = request.getParameter("department")
						.isEmpty() ? "" : request.getParameter("department");
				String information = mailAdd;
				String rankString = request.getParameterMap().containsKey("rank") ?  request.getParameter("rank") : "" ;
				String feeString = request.getParameterMap().containsKey("fee") ?  request.getParameter("fee") : "" ;
				String checkString = information + department + issuetype
						+ description + summary + rankString + feeString;

				// Map<String, Object> infoList = new HashMap<String, Object>();
				if (jirasiteUrl.isEmpty() || Project.isEmpty()
						|| summary.isEmpty() || description.isEmpty()
						|| issuetype.isEmpty() || information.isEmpty()
						|| department.isEmpty() || feeString.isEmpty() || rankString.isEmpty()) {
					String warn = "创建失败，字段不能为空";
					ctx.put("warn", warn);
					return nulltemplate;
				} else if (!CommonUtil.checkStringValidation(checkString,
						checkPat)) {
					ctx.put("warn",
							"输入字符不能包含以下字符：" + StringUtils.join(checkPat, " "));
					return nulltemplate;
				} else if (!CommonUtil.checkStringValidation(feeString,
						numberCheckPat)) {
					ctx.put("warn",
							"费用字段输入字符只能是数字");
					return nulltemplate;
				}else {
					String responseStr = "";
					try {
						responseStr = issueInfo.createIssue(jirasiteUrl,
								Project, summary, description, issuetype,
								aduname, adpassword, information, department,
								displayName, feeString, rankString);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (responseStr.isEmpty()) {
						String warn = "创建事件失败";
						ctx.put("warn", warn);
						return nulltemplate;
					} else {
						ctx.put("warn", responseStr + "创建成功");

						String attachNameList = request.getParameter(
								"attachNameList").isEmpty() ? "" : request
								.getParameter("attachNameList");
						if (!attachNameList.isEmpty()) {
							System.out.println(attachNameList);
							String[] attachListArray = attachNameList
									.split(",");
							for (int i = 0; i < attachListArray.length; i++) {
								if (!attachListArray[i].trim().isEmpty()) {
									UploadFileToJira UFJ = new UploadFileToJira(
											UPLOADFILE,
											path + PROPERTYNAME_FILE
													+ attachListArray[i].trim(),
											responseStr, issueInfo);
									Thread UFJT = new Thread(UFJ);
									UFJT.start();
								}
							}
						} else {
							System.out.println("attachemnt is empty");
						}

						return nulltemplate;
					}
				}
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ctx.put("warn", "创建失败，缺少字段");
			}
		}

		return nulltemplate;
	}
}
