package com.velocitydemo.velocityhandler;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.velocity.tools.view.VelocityViewServlet;

public class AjaxUploadFile extends VelocityViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50 * 1024* 1024;
	private int maxMemSize = 50 * 1024* 1024;
	private File file;
	private static String userName = "userName";
	private static String userPassword = "userPassword";
	
	private String[] checkPat = { "=", "\"", "'", "\\\\", "/", " ", ","};

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getRealPath("/").toString()
				+ "temp\\upload\\";
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		
		JSONObject outputJSON = new JSONObject();
		
		
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("application/json; charset=utf-8");
		java.io.PrintWriter out = response.getWriter();
		
		if (!IsLoggedIn.checkLogin(this, response, request)) {
			outputJSON.put("error","login");
			out.println(outputJSON.toString());
			return;
		}
		
		if (!isMultipart) {
			outputJSON.put("error","没有上传文件");
			out.println(outputJSON.toString());
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("c:\\temp"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		try {
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					
					if(!CommonUtil.checkStringValidation(fileName, checkPat)){
						outputJSON.put("error","文件名不能包含非法字符和空格");
						out.println(outputJSON.toString());
						return;
					}
					  
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					// Write the file
					if (fileName.lastIndexOf("\\") >= 0) {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\")));
					} else {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\") + 1));
					}
					fi.write(file);
				}
			} 
			outputJSON.put("success","success");
			out.println(outputJSON.toString());
			return;
		} catch (Exception ex) {
			System.out.println(ex);
			outputJSON.put("error",ex.toString());
			out.println(outputJSON.toString());
			return;
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();
		if (!IsLoggedIn.checkLogin(this, response, request)) {
			out.println("login");
			return;
		}
		out.println(" <html><head><title>File Uploading Form</title></head><body><h3>File Upload:</h3>Select a file to upload: <br /><form action='AjaxUploadFile' method='post' enctype='multipart/form-data'><input type='file' name='file' size='50' /><br /><input type='submit' value='Upload File' /></form></body></html>");
	}
}