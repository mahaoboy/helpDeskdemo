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

public class HelpDesk extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String loginPath = StaticConstantVar.loginPath;
	private static String userName = StaticConstantVar.userName;
	private static String userDisplayName = StaticConstantVar.userDisplayName;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		path = this.getServletContext().getRealPath("/");
		prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + StaticConstantVar.tempPath);
		prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "GBK");

		try {
			velo.init(prop);// 初始化设置，下面用到getTemplate("*.vm")输出时;一定要调用velo对象去做,即velo.getTemplate("*.vm")
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod();
		String uname = "";
		String displayName = null;

		if (IsLoggedIn.checkLogin(this, response, request)) {
			uname = IsLoggedIn.getUserInfo(this, response, request, userName);
			displayName = IsLoggedIn.getUserInfo(this, response, request,
					userDisplayName);
		} else {
			try {
				response.sendRedirect(loginPath);
				Template nulltemplate = new Template();
				try {
					nulltemplate = velo.getTemplate("HelpDesk.vm");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return nulltemplate;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (meth.equals("POST")) {

		} else if (meth.equals("GET")) {
		}

		ctx.put("aduname", displayName);
		response.setContentType("text/html; charset=gb2312");
		Template template = new Template();
		try {
			template = velo.getTemplate("HelpDesk.vm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}
}
