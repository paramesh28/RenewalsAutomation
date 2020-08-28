package com.salesforce.automation.commonAPI;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class WriteOutput {

	public void write(Properties prop, String ENV, String Filename){

		OutputStream CPQ_OUTPUT_FILE = null;

		try {
			CPQ_OUTPUT_FILE = new FileOutputStream(System.getProperty("user.dir")+"/src/main/resources/"+Filename+".properties");
			prop.store(CPQ_OUTPUT_FILE, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (CPQ_OUTPUT_FILE != null) {
				try {
					CPQ_OUTPUT_FILE.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
