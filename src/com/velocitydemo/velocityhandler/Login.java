package com.velocitydemo.velocityhandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class Login extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
	private VelocityEngine velo;
	private String path;
	private String PROPERTYNAME = "WEB-INF\\ldap.conf";
	private static HashMap<String, String> properties = new HashMap<String, String>();
	private LDAPAuthentication ldapc;

	public void init() throws ServletException {
		this.velo = new VelocityEngine();// velocity引擎对象
		Properties prop = new Properties();// 设置vm模板的装载路径
		// String path =
		// this.getClass().getClassLoader().getResource("").getPath();
		path = this.getServletContext().getRealPath("/");
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
				readFile(path + PROPERTYNAME);
			}
			ldapc = new LDAPAuthentication(properties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		String meth = request.getMethod();
		String uname = "";

		if (IsLoggedIn.checkLogin(response, request)) {
			uname = IsLoggedIn.getUser(response, request);
		}

		if (meth.equals("POST")) {
			String inusername = request.getParameter("username").isEmpty() ? ""
					: request.getParameter("username");
			String inpassword = request.getParameter("password").isEmpty() ? ""
					: request.getParameter("password");
			if (inusername.isEmpty() || inpassword.isEmpty()) {
				String warn = "用户名或密码不能为空";
				ctx.put("meth", warn);
			} else {
				if (ldapc.authenricate(inusername, inpassword)) {
					IsLoggedIn.setLogin(response, request, inusername);
					ctx.put("meth", inusername);
				} else {
					String warn = "登陆失败,用户名或密码错误";
					ctx.put("meth", warn);
				}
			}
		} else if (meth.equals("GET")) {
			ctx.put("meth", "");
		}
		
		ctx.put("uname", uname);
		response.setContentType("text/html; charset=gb2312");
		Template template = new Template();
		try {
			template = velo.getTemplate("login.vm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}

	public void readFile(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		String[] part;
		BufferedReader br = new BufferedReader(fr);
		try {
			String line = br.readLine();
			while (line != null) {
				part = line.split(":");
				properties.put(
						part[0].trim(),
						StringUtils.join(
								Arrays.copyOfRange(part, 1, part.length), "")
								.trim());
				line = br.readLine();
			}
		} finally {
			br.close();
			fr.close();
		}
	}

}