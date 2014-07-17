package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.tools.view.VelocityViewServlet;

public class IsLoggedIn {
	private static String IsLoggedInString = StaticConstantVar.IsLoggedInString;
	private static String userName = StaticConstantVar.userName;
	private static String userPassword = StaticConstantVar.userPassword;
	
	private static String userDisplayName = StaticConstantVar.userDisplayName;
	private static String SESSION_COOKIE_NAME = StaticConstantVar.SESSION_COOKIE_NAME;
	private static String PROPERTYNAME = StaticConstantVar.LDAP_PROPERTYNAME;
	private static String JIRA_PROPERTYNAME = StaticConstantVar.JIRA_PROPERTYNAME;
	private static HashMap<String, String> properties = new HashMap<String, String>();
	private static HttpServlet scontext;
	private static String adminUserName;
	private static String adminPassWord;
	
	private static boolean getAdminInfo() {
		String path = scontext.getServletContext().getRealPath("/");
		try {

			properties = CommonUtil.readFile(path + JIRA_PROPERTYNAME);
			adminUserName = properties.get("Username");
			adminPassWord = properties.get("Password");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private static boolean isLDAPenabled() {
		String path = scontext.getServletContext().getRealPath("/");
		try {

			properties = CommonUtil.readFile(path + PROPERTYNAME);
			if (properties.get("LDAP").equals("disabled")) {
				return false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static boolean checkLogin(HttpServlet servletContext,
			HttpServletResponse response, HttpServletRequest request) {
		/*
		 * PrintWriter out; try { out = response.getWriter();
		 * 
		 * out.println("Hit the browsers refresh button.");
		 */

		scontext = servletContext;
		if (!isLDAPenabled()) {
			getAdminInfo();
			setLogin(response, request, adminUserName, adminPassWord, adminUserName);
			return true;
		}
		Cookie cookies[] = request.getCookies();
		Boolean loginOrNot = false;

		Cookie c1 = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				c1 = cookies[i];

				if (SESSION_COOKIE_NAME.equals(c1.getName())) {
					HttpSession session = HttpSessionCollector.find(c1
							.getValue());
					if (session != null) {
						loginOrNot = (Boolean) session
								.getAttribute(IsLoggedInString);
						// System.out.println("loginOrNot : " + loginOrNot);
					}

				}
			}
		}

		return loginOrNot;

	}

	public static void setLogin(HttpServletResponse response,
			HttpServletRequest request, String userid , String inpassword, String displayNameofUser) {
		HttpSession session = request.getSession();
		HttpSessionCollector.sessionadded(session);

		session.setMaxInactiveInterval(60 * 60 * 12);
		if (!userid.isEmpty()) {
			session.setAttribute(IsLoggedInString, true);
			session.setAttribute(userName, userid);
			session.setAttribute(userPassword, inpassword);
			session.setAttribute(userDisplayName, displayNameofUser);
		}

		Cookie cookies[] = request.getCookies();

		Cookie c = null;
		Cookie cl = null;
		if (session != null && cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				c = cookies[i];
				if (c.getName().equals(SESSION_COOKIE_NAME)) {
					c.setValue(session.getId());
					c.setMaxAge(60 * 60 * 12);
					cl = c;
					break;
				}
			}
			if (cl == null) {
				c = new Cookie(SESSION_COOKIE_NAME, session.getId());
				c.setMaxAge(60 * 60 * 12);
			}
		} else {
			c = new Cookie(SESSION_COOKIE_NAME, session.getId());
			c.setMaxAge(60 * 60 * 12);
		}

		response.addCookie(c);
	}

	public static String getUserInfo(HttpServlet servletContext,
			HttpServletResponse response, HttpServletRequest request, String userinfo) {
		if (!IsLoggedIn.checkLogin(servletContext, response, request)) {
			return "";
		} else {
			Cookie cookies[] = request.getCookies();
			String uinfo = "";
			Cookie c1 = null;
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					c1 = cookies[i];
					if (SESSION_COOKIE_NAME.equals(c1.getName())) {
						HttpSession session = HttpSessionCollector.find(c1
								.getValue());
						if (session != null && userinfo.equals(userName)) {
							uinfo = (String) session.getAttribute(userName);
						}else if (session != null && userinfo.equals(userPassword)){
							uinfo = (String) session.getAttribute(userPassword);
						}else if (session != null && userinfo.equals(userDisplayName)){
							uinfo = (String) session.getAttribute(userDisplayName);
						}
					}
				}
			}
			return uinfo;
		}

	}
}
