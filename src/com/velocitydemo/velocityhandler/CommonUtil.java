package com.velocitydemo.velocityhandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
}
