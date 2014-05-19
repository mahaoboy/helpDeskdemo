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

public class CreateIssueEvent extends VelocityViewServlet {
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
	private String searchPath = "/SearchIssueEvent";

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity�������
		Properties prop = new Properties();// ����vmģ���װ��·��
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + "temp");
		prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "GBK");

		try {
			velo.init(prop);// ��ʼ�����ã������õ�getTemplate("*.vm")���ʱ;һ��Ҫ����velo����ȥ��,��velo.getTemplate("*.vm")
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
			issueInfo.setAduserName(properties.get("����"));
			issueInfo.setDepartmentName(properties.get("����"));
			issueInfo.setInformationName(properties.get("��ϵ�绰�ʼ���ַ"));
			issueInfo.setJirasite(jirasiteUrl);
			issueInfo.setProject(Project);
			issueInfo.setUsername(Username);
			issueInfo.setPassword(Password);
		}
	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod();
		String aduname = null;

		if (IsLoggedIn.checkLogin(response, request)) {
			aduname = IsLoggedIn.getUser(response, request);
		} else {
			try {
				response.sendRedirect(loginPath);
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
			String information = request.getParameter("information").isEmpty() ? ""
					: request.getParameter("information");

			// Map<String, Object> infoList = new HashMap<String, Object>();
			if (jirasiteUrl.isEmpty() || Project.isEmpty() || summary.isEmpty()
					|| description.isEmpty() || issuetype.isEmpty()
					|| Username.isEmpty() || Password.isEmpty()
					|| information.isEmpty() || department.isEmpty()) {
				String warn = "����ʧ�ܣ��ֶβ���Ϊ��";
				ctx.put("warn", warn);
			} else {
				String responseStr = "";
				try {
					responseStr = issueInfo.createIssue(jirasiteUrl, Project,
							summary, description, issuetype, Username,
							Password, information, department, aduname);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (responseStr.isEmpty()) {
					String warn = "�����¼�ʧ��";
					ctx.put("warn", warn);

				} else {
					request.setAttribute("Createdissuekey", responseStr);
					request.setAttribute("Createdissuelink",
							issueInfo.getIssueLink());
					RequestDispatcher rd = null;
					rd = this.getServletContext().getRequestDispatcher(
							searchPath);
					try {
						rd.forward(request, response);
					} catch (ServletException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						String warn = "�����¼��ɹ��� " + responseStr;
						ctx.put("warn", warn);
					}
				}
			}
			System.out.println(" +++++++ " + jirasiteUrl + " +++++++ "
					+ Project + " +++++++ " + summary + " +++++++ "
					+ description + " +++++++ " + issuetype + " +++++++ "
					+ Username + " +++++++ " + Password);

			ctx.put("summary", summary);
			ctx.put("description", description);
			ctx.put("department", department);
			ctx.put("information", information);
			ctx.put("issuetype", issuetype);
		}
		// read file store
		Vector itemlist;
		try {
			itemlist = issueInfo.getIssueTypes();
			ctx.put("itemlist", itemlist);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		response.setContentType("text/html; charset=gb2312");
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