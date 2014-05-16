package com.velocitydemo.velocityhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Servlet implementation class getPara
 */
@WebServlet("/getPara")
public class GetPara extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPara() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String path = this.getServletContext().getRealPath("/").toString() + "temp";
		pageInfo pageInfoI = new pageInfo(path);
		
        response.setContentType("text/html;charset=gb2312");
        PrintWriter out = response.getWriter();
        
        String pageIdI = "";
        		
        try{
        	pageIdI = pageInfoI.getPageId();
        }catch(Exception e){
        	e.printStackTrace();
        	out.println("无法获取锚点"); 
        }
        
        String pagebody = pageInfoI.getPageBody();
		if(!pageIdI.isEmpty() && !pagebody.isEmpty()){
			
			out.println(StringEscapeUtils.unescapeHtml(pagebody));
		}else{
			out.println("无法获取锚点"); 
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
