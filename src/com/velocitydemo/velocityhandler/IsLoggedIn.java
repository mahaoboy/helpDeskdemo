package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class IsLoggedIn {
	private static String IsLoggedInString = "IsLoggedIn";
	private static String userName = "userName";
	private static String SESSION_COOKIE_NAME = "SESSION_COOKIE";

	public static boolean checkLogin(HttpServletResponse response,
			HttpServletRequest request) {
		/*
		 * PrintWriter out; try { out = response.getWriter();
		 * 
		 * out.println("Hit the browsers refresh button.");
		 */
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
						//System.out.println("loginOrNot : " + loginOrNot);
					}

				}
			}
		}
		/*
		 * } catch (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		return loginOrNot;

	}

	public static void setLogin(HttpServletResponse response,
			HttpServletRequest request, String userid) {
		HttpSession session = request.getSession();
		HttpSessionCollector.sessionadded(session);
		
		session.setMaxInactiveInterval(60 * 60 * 12);
		if (!userid.isEmpty()) {
			session.setAttribute(IsLoggedInString, true);
			session.setAttribute(userName, userid);
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

	public static String getUser(HttpServletResponse response,
			HttpServletRequest request) {
		if (!IsLoggedIn.checkLogin(response, request)) {
			return "";
		} else {
			Cookie cookies[] = request.getCookies();
			String user = "";
			Cookie c1 = null;
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					c1 = cookies[i];
					if (SESSION_COOKIE_NAME.equals(c1.getName())) {
						HttpSession session = HttpSessionCollector.find(c1
								.getValue());
						if (session != null) {
							user = (String) session.getAttribute(userName);
						}

					}
				}
			}
			return user;
		}

	}
}
