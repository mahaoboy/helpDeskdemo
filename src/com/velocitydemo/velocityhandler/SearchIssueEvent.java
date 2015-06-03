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
	private String colsedStatusForSearch;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;
	private static String userDisplayName = StaticConstantVar.userDisplayName;
	private static String EMPTY_VALUE = StaticConstantVar.EMPTY_VALUE;

	private String[] checkPat = StaticConstantVar.checkPat;
	private String[] numberCheckPat = StaticConstantVar.numberPat;
	private String checkForDate = StaticConstantVar.checkForDate;

	private String PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();

	private String loginPath = StaticConstantVar.loginPath;
	private String searchPath = StaticConstantVar.searchPath;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity�������
		Properties prop = new Properties();// ����vmģ���װ��·��
		path = this.getServletContext().getRealPath("/");
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path
				+ StaticConstantVar.tempPath);
		prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");

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
			// this.Username = properties.get("Username");
			// this.Password = properties.get("Password");
			this.deFaultStatusForSearch = properties.get("Ĭ�������¼�״̬");
			this.colsedStatusForSearch = properties.get("�ر�״̬");
			issueInfo.setDepartmentName(properties.get("���벿��"));
			//issueInfo.setInformationName(properties.get("��ϵ��ʽ"));
			issueInfo.setResolutionDetailName(properties.get("IT�������"));
			issueInfo.setWorkAround(properties.get("����ʽ"));
			issueInfo.setReasonOfNotPass(properties.get("δͨ��ԭ��"));
			issueInfo.setIssuetype(properties.get("issueType"));
			issueInfo.setEventType(properties.get("������������"));
			issueInfo.setSpecificDescription(properties.get("����"));
			issueInfo.setJirasite(jirasiteUrl);
			issueInfo.setProject(Project);
			issueInfo.setCloseAction(properties.get("�ر�״̬"));
			issueInfo.setReopenAction("�ؿ�����");
			issueInfo.setEstimateForAccomplish(properties.get("Ԥ���������"));
			issueInfo.setReallistOfAccomplish(properties.get("ʵ�ʴ����������"));
			issueInfo.setRank(properties.get("ְ��"));
			issueInfo.setFee(properties.get("����"));
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

		if (IsLoggedIn.checkLogin(this, response, request)) {
			aduname = IsLoggedIn.getUserInfo(this, response, request, userName);
			adpassword = IsLoggedIn.getUserInfo(this, response, request,
					userPassword);
			displayName = IsLoggedIn.getUserInfo(this, response, request,
					userDisplayName);
			ctx.put("aduname", displayName);

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
		ctx.put("searchResultPrefix", "");
		ctx.put("meth", meth);
		// System.out.println("Method:" + meth);

		if (meth.equals("POST")
				&& request.getAttribute("Createdissuekey") == null) {
			String checkString = "";
			try {
				String summary = request.getParameter("summary").isEmpty() ? ""
						: request.getParameter("summary");
				String description = request.getParameter("description")
						.isEmpty() ? "" : request.getParameter("description");
				String issuetype = request.getParameter("issuetype").isEmpty() ? ""
						: request.getParameter("issuetype");
				String department = request.getParameter("department")
						.isEmpty() ? "" : request.getParameter("department");
				String information = "";

				String createtime = request.getParameter("createtime")
						.isEmpty() ? "" : request.getParameter("createtime");
				String createtimeend = request.getParameter("createtimeend")
						.isEmpty() ? "" : request.getParameter("createtimeend");
				String status = request.getParameter("status").isEmpty() ? ""
						: request.getParameter("status");
				String resolution = "";
				
				String assigned = request.getParameter("assigned").isEmpty() ? ""
						: request.getParameter("assigned");
				
				String resolutiondescription = request.getParameter(
						"resolutiondescription").isEmpty() ? "" : request
						.getParameter("resolutiondescription");
				String reasonOfNotPass = request
						.getParameter("reasonOfNotPass").isEmpty() ? ""
						: request.getParameter("reasonOfNotPass");
				
				String rankString = request.getParameterMap().containsKey("rank") ?  request.getParameter("rank") : "" ;
				String feeString = request.getParameterMap().containsKey("fee") ?  request.getParameter("fee") : "" ;

				checkString += resolutiondescription + resolution + status
						+ createtime + information + department + issuetype
						+ description + summary + reasonOfNotPass + assigned + rankString + feeString;

				if ((!createtime.isEmpty() && !CommonUtil.checkStringValidat(
						createtime, checkForDate))
						|| (!createtimeend.isEmpty() && !CommonUtil
								.checkStringValidat(createtimeend, checkForDate))) {
					ctx.put("warn", "���ڸ�ʽ����: yyyy-mm-dd");
				} else if (!CommonUtil.checkStringValidation(checkString,
						checkPat)) {
					ctx.put("warn",
							"�����ַ����ܰ��������ַ���" + StringUtils.join(checkPat, " "));
				}else if (!CommonUtil.checkStringValidation(feeString,
						numberCheckPat)) {
					ctx.put("warn",
							"�����ֶ������ַ�ֻ��������");
				} else {
					try {
						if (aduname.equals(null)) {
							try {
								response.sendRedirect(loginPath);
								Template nulltemplate = new Template();
								try {
									nulltemplate = velo
											.getTemplate("SearchIssueEvent.vm");
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
									.searchIssue(displayName, summary,
											description, issuetype, department,
											information, createtime,
											createtimeend, status, resolution,
											resolutiondescription,
											reasonOfNotPass, assigned, feeString, rankString);
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
				//ctx.put("information", information);
				ctx.put("createtime", createtime);
				ctx.put("createtimeend", createtimeend);
				ctx.put("status", status);
				ctx.put("resolution", resolution);
				ctx.put("resolutiondescription", resolutiondescription);
				ctx.put("reasonOfNotPass", reasonOfNotPass);
				ctx.put("assigned", assigned);
				ctx.put("showSearchCretiria", true);
				ctx.put("fee", feeString);
				ctx.put("rank", rankString);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					response.sendRedirect(searchPath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			/*
			 * } else if (meth.equals("GET")) { try { if (aduname.equals(null))
			 * { try { response.sendRedirect(loginPath); Template nulltemplate =
			 * new Template(); try { nulltemplate = velo
			 * .getTemplate("SearchIssueEvent.vm"); } catch (Exception e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); } return
			 * nulltemplate; } catch (IOException e) { e.printStackTrace(); } }
			 * else { Vector<Object> searchResultViaJQL = issueInfo.searchIssue(
			 * displayName, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
			 * EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
			 * this.deFaultStatusForSearch, EMPTY_VALUE, EMPTY_VALUE,
			 * EMPTY_VALUE); ctx.put("searchResultViaJQL", searchResultViaJQL);
			 * ctx.put("total", issueInfo.getTotalNumber()); } } catch
			 * (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
		} else {
			try {
				if (aduname.equals(null)) {
					try {
						response.sendRedirect(loginPath);
						Template nulltemplate = new Template();
						try {
							nulltemplate = velo
									.getTemplate("SearchIssueEvent.vm");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return nulltemplate;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					String IssueStatus = request.getParameterMap().containsKey(
							"IssueStatus") ? request
							.getParameter("IssueStatus") : "";
					String showSearchCretiria = request.getParameterMap().containsKey(
									"showSearchCretiria") ? request
									.getParameter("showSearchCretiria") : "";
					Vector<Object> searchResultViaJQL;

					if (IssueStatus.equals(StaticConstantVar.notClosed)) {
						issueInfo.setExtraJQLString(" and status != '"
								+ this.colsedStatusForSearch + "'");
						searchResultViaJQL = issueInfo.searchIssue(displayName,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE);
						ctx.put("searchResultPrefix", "�������ύ��δ�ر�����");
					} else if (IssueStatus.equals(StaticConstantVar.all)) {
						issueInfo.setExtraJQLString("");
						searchResultViaJQL = issueInfo.searchIssue(displayName,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE);
						ctx.put("searchResultPrefix", "�������ύ������");
					} else {
						issueInfo.setExtraJQLString("");
						searchResultViaJQL = issueInfo.searchIssue(displayName,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE,
								EMPTY_VALUE, this.deFaultStatusForSearch,
								EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE);
						ctx.put("searchResultPrefix", "���ύ�Ѵ�����ɵģ���Ҫ��ȷ���Ƿ�ͨ��������");
					}
					ctx.put("searchResultViaJQL", searchResultViaJQL);
					ctx.put("showSearchCretiria", showSearchCretiria);
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
		Vector departMent;
		
		try {
			issuetypes = issueInfo.getEventTypes();
			ctx.put("issuetypes", issuetypes);

			statuses = issueInfo.getIssueStatus();
			ctx.put("statuss", statuses);

/*			resolutions = issueInfo.getWorkAroundTypes();
			ctx.put("resolutions", resolutions);*/

			departMent = issueInfo.getCreateMeta(issueInfo.getDepartmentName());
			ctx.put("departmentitem", departMent);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			String warn = "��JIRA��ȡ��Ϣʧ��";
			ctx.put("warn", warn);
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
