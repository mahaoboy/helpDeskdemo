package com.velocitydemo.velocityhandler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class Confluence extends VelocityViewServlet {
	
	
    private static final long serialVersionUID = 1L;
    private VelocityEngine velo;
    public void init() throws ServletException {
            this.velo = new VelocityEngine();// velocity引擎对象
            Properties prop = new Properties();// 设置vm模板的装载路径
            String path = this.getServletContext().getRealPath("/");
            prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path + "temp");
            prop.setProperty(Velocity.INPUT_ENCODING, "GBK");
            prop.setProperty(Velocity.OUTPUT_ENCODING, "GBK");
            
            try {
                    velo.init(prop);// 初始化设置，下面用到getTemplate("*.vm")输出时;一定要调用velo对象去做,即velo.getTemplate("*.vm")
            } catch (Exception e1) {
                    e1.printStackTrace();
            }
    }
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
    	pageInfo pageInfoI = new pageInfo(velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
    	String meth = request.getMethod();
    	ctx.put("meth",meth);
    	ctx.put("filepath",velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
    	//System.out.println("Method:" + meth);
    	if (meth == "POST"){
        	String confluencesite = request.getParameter("confluencesite").isEmpty()?"":request.getParameter("confluencesite");
        	String summary = request.getParameter("summary").isEmpty()?"":request.getParameter("summary");
        	//String contentbody = request.getParameter("contentbody").isEmpty()?"":request.getParameter("contentbody");
        	String username = request.getParameter("username").isEmpty()?"":request.getParameter("username");
        	String password = request.getParameter("password").isEmpty()?"":request.getParameter("password");
        	         
        	//Map<String, Object> infoList = new HashMap<String, Object>();
        	if (confluencesite.isEmpty() || summary.isEmpty()|| username.isEmpty() || password.isEmpty()){
        		String warn = "锚点创建失败";
        		ctx.put("warn", warn);
        	}
        	else{
        		pageInfoI.getPage(confluencesite, summary,  username, password);
        		try{
        		if(pageInfoI.getPageId().isEmpty()){
            		String warn = "锚点创建失败";
            		ctx.put("warn", warn);
        		}else{
        			pageInfoI.writeFile(velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
        		}
        		}catch(Exception e){
        			e.printStackTrace();
            		String warn = "锚点创建失败";
            		ctx.put("warn", warn);
        		}
        		

           	}
        
        	ctx.put("confluencesite",confluencesite);
        	ctx.put("summary",summary);
        	ctx.put("username",username);
    	}
    	try{
	    	if (!pageInfoI.getSummary().isEmpty()){
	    		ctx.put("summary",pageInfoI.getSummary());
	        	ctx.put("confluencesite",pageInfoI.getConfluencesite());
	        	ctx.put("username",pageInfoI.getUsername());
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}

		response.setContentType("text/html; charset=gb2312");
        Template template = new Template();
		try {
			template = velo.getTemplate("Confluence.vm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return template;
    }
}