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

public class JIRA extends VelocityViewServlet {
		
		private IssueInfo issueInfo = new IssueInfo();
        private static final long serialVersionUID = 1L;
        private VelocityEngine velo;
        public void init() throws ServletException {
                this.velo = new VelocityEngine();// velocity引擎对象
                Properties prop = new Properties();// 设置vm模板的装载路径
               // String path = this.getClass().getClassLoader().getResource("").getPath();
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
        	String meth = request.getMethod();
        	ctx.put("meth",meth);
        	//System.out.println("Method:" + meth);
        	if (meth == "POST"){
            	String jirasite = request.getParameter("jirasite").isEmpty()?"":request.getParameter("jirasite");
            	String project = request.getParameter("project").isEmpty()?"":request.getParameter("project");
            	String summary = request.getParameter("summary").isEmpty()?"":request.getParameter("summary");
            	String description = request.getParameter("description").isEmpty()?"":request.getParameter("description");
            	
            	String issuetype = request.getParameter("issuetype").isEmpty()?"":request.getParameter("issuetype");
            	String username = request.getParameter("username").isEmpty()?"":request.getParameter("username");
            	String password = request.getParameter("password").isEmpty()?"":request.getParameter("password");
            	         
            	//Map<String, Object> infoList = new HashMap<String, Object>();
            	if (jirasite.isEmpty() || project.isEmpty() || summary.isEmpty() || description.isEmpty() || issuetype.isEmpty() || username.isEmpty() || password.isEmpty()){
            		String warn = "工单创建失败";
            		ctx.put("warn", warn);
            	}
            	else{
            		
/*            		if(issuetype.equals("Task"))
            			System.out.println("issue type:" + issuetype);
            		
            		Pattern pattern = Pattern.compile("Task");
            		Matcher matcher = pattern.matcher(issuetype);
            		boolean urlCheck= matcher.matches();*/
            		if(!issuetype.equals("saab")){

            	/*		String issuetype1 = "";
						try {
							issuetype1 = new String(issuetype.getBytes("ISO-8859-1"),"GBK");// 任务
							issuetype = issuetype1;
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
            			
            			System.out.println("issue type 2:" + issuetype);
            			String responseStr = "";
                		try {
                			responseStr = issueInfo.createIssue(jirasite, project, summary, description, issuetype, username, password);
                			
    					} catch (Exception e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                		if (responseStr.isEmpty()){
                			String warn = "创建 JIRA Issue 失败, 工单创建失败";
                			ctx.put("warn", warn);
                			
                		}else{
                			ctx.put("issuekey",responseStr);
                			ctx.put("issuelink",issueInfo.getIssueLink());
                			issueInfo.writeFile(velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
                		}
            		}else{
            			issueInfo.createIssue(project, summary, description, issuetype, username);
            			issueInfo.writeFile(velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
            		}
            		System.out.println(" +++++++ " + jirasite + " +++++++ " + project + " +++++++ " + summary + " +++++++ " + description + " +++++++ " +  issuetype + " +++++++ " +  username + " +++++++ " +  password);

            	}            		
            		                        	
            	ctx.put("jirasite",jirasite);
            	ctx.put("project",project);
            	ctx.put("summary",summary);
            	ctx.put("description",description);
            	ctx.put("issuetype",issuetype);
            	ctx.put("username",username);
            	
        		}
        	
        		//read file store
        		Vector itemlist = issueInfo.getItemList(velo.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH).toString());
        		ctx.put("itemlist",itemlist);
        		
        		response.setContentType("text/html; charset=gb2312");
                Template template = new Template();
				try {
					template = velo.getTemplate("JIRA.vm");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return template;
        }
}         