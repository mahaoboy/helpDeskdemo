package com.velocitydemo.velocityhandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

import org.apache.commons.lang.StringUtils;

public class CommonUtil {
	public static HashMap<String, String> readFile(String fileName) throws IOException {
		HashMap<String, String> properties = new HashMap<String, String>();
		if(!fileName.isEmpty()){
			File fl = new File(fileName);
	        if(!fl.exists()){
	        	return properties;
	        }
		}
		else{
			return properties;
		}
		
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
		
		return properties;
	}
	
	public static boolean checkStringValidation(String strForCheck, String[] checkPat){
		Pattern pattern;
		Matcher matcher;
		boolean urlCheck =false;
		
		for(int i=0; i<checkPat.length; i++){
			if(!checkPat[i].isEmpty()){
				pattern= Pattern.compile(".*"+checkPat[i]+".*");
				matcher = pattern.matcher(strForCheck);
				
				urlCheck = matcher.matches();
				if (urlCheck) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static String formatDateFromString(String dateString, String inputFormat, String outputString) throws ParseException{
		SimpleDateFormat sd = new SimpleDateFormat(inputFormat);
		DateFormat  sd1 = new SimpleDateFormat(outputString);
		Date d = sd.parse(dateString);
		String outDate = sd1.format(d);
		return outDate;
	}
	
	public static boolean checkStringValidat(String strForCheck, String checkStr){
		Pattern pattern;
		Matcher matcher;
		boolean urlCheck =false;

		pattern= Pattern.compile(checkStr);
		matcher = pattern.matcher(strForCheck);
		
		urlCheck = matcher.matches();
		if (urlCheck) {
			return true;
		}

		return false;
	}
	
	
	
}
