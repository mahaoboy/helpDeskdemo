package com.velocitydemo.velocityhandler;

import java.io.File;

public class StaticConstantVar {
	public static String LDAP_PROPERTYNAME = "WEB-INF" + File.separator
			+ "ldap.conf";
	public static String JIRA_PROPERTYNAME = "WEB-INF" + File.separator
			+ "jira.conf";
	public static String IsLoggedInString = "IsLoggedIn";
	public static String userName = "userName";
	public static String userPassword = "userPassword";
	public static String userDisplayName = "userDisplayName";
	public static String userMailAdd = "userMailAdd";
	public static String[] checkPat = { "=", "\"", "'", "\\\\", "/" };
	public static String[] numberPat = {"[^.0-9]"};
	public static String PROPERTYNAME_FILE_UPLOAD = "temp" + File.separator
			+ "upload" + File.separator;
	public static String UPLOADFILE = "UPLOADFILE";
	public static String createPath = "CreateIssueEvent";
	public static String searchPath = "SearchIssueEvent";
	public static String loginPath = "Login";
	public static String tempPath = "temp";
	public static String[] UploadCheckPat = { "=", "\"", "'", "\\\\", "/", " ",
			",", "%", ":", "\\$", "\\?", "\\*" };
	public static String tempPathSystem = File.separator + "tmp";
	public static String SESSION_COOKIE_NAME = "SESSION_COOKIE";
	
	public static String jiraLoginPath = "login.jsp";
	public static String os_username = "os_username";
	public static String os_password = "os_password";
	public static String os_destination = "os_destination";
	public static String destinationPath = "/secure/attachment/";
	public static String destinationPathDashboard = "/";
	public static String HelpDeskPath = "HelpDesk";
	public static String checkForDate = "^\\d{4}-\\d{2}-\\d{2}$";
	
	public static String EMPTY_VALUE = "";
	public static String notClosed = "notClosed";
	public static String all = "all";
	public static String userJIRApass = "userJIRApass";
}
