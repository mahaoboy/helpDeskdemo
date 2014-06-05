package com.velocitydemo.velocityhandler;

import java.io.File;
import java.io.IOException;


public class UploadFileToJira implements Runnable {
    private String name; 
    private String filepath;
    private String issueKey;
    private IssueInfo issueInfo;

    public UploadFileToJira(String name, String filepath, String issueKey, IssueInfo issueInfo) { 
        this.name = name; 
        this.filepath = filepath; 
        this.issueKey = issueKey; 
        this.issueInfo = issueInfo; 
    } 
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			File f = new File(filepath);
			if(f.exists() && !f.isDirectory()){
				if(issueInfo.addAttachmentToIssue(issueKey, filepath)){
					System.out.println(filepath + "uploaded sucessfully");
				}else{
					System.out.println(filepath + "uploaded failed");
				}
			}else{
				System.out.println(filepath + "file not existed or is a directory");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
