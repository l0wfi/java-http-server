package com.project.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationManager {
	
	private static File configFile;
	
	private static final String HOME_DIR = System.getProperty("user.home");
	private static final String PROGRAM_DIR = HOME_DIR + "/.JHttpServer"; 
	private static final int DEFAULT_PORT = 8080;
	private static final int DEFAULT_THREAD_COUNT = 3;
	private static final String DEFAULT_ROOT = PROGRAM_DIR + "/public_html";
	
	private Properties prop = new Properties();
	
	public ConfigurationManager(String configPath) {
		this.configFile = new File(configPath);
		if (configFile.exists()) {
			try (FileInputStream fis = new FileInputStream(configFile)) {
				// Load the config file in the properties object
				prop.load(fis);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			createConfigFolder(configPath);
			createDefaultConfig();
		}
	}
	
	public void createConfigFolder(String configPath) {
		File progPath = new File(PROGRAM_DIR);
		if(!Files.exists(Paths.get(PROGRAM_DIR))) {
			progPath.mkdir();
		}
		
		int index = configPath.lastIndexOf('/');
	    String dir = configPath.substring(0,index);
	    
	    File confPath = new File(dir);
		if(!Files.exists(Paths.get(dir))) {
			confPath.mkdir();
		}
	}
	
	public void createDefaultConfig() {
		// Set the default values in the config file
		try {
		    prop.setProperty("PORT", Integer.toString(DEFAULT_PORT));
		    prop.setProperty("THREAD_COUNT", Integer.toString(DEFAULT_THREAD_COUNT));
		    prop.setProperty("ROOT_DIR", DEFAULT_ROOT);
		    FileWriter writer = new FileWriter(configFile);
		    prop.store(writer, "HTTP server settings");
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefault(String parameter) {
		switch (parameter) {
		case "PORT": {
			prop.setProperty("PORT", Integer.toString(DEFAULT_PORT));
			break;
		}
		case "THREAD_COUNT": {
			prop.setProperty("THREAD_COUNT", Integer.toString(DEFAULT_THREAD_COUNT));
			break;
		}
		case "ROOT_DIR": {
			prop.setProperty("ROOT_DIR", DEFAULT_ROOT);
			break;
		}
		}
		try {
			FileWriter writer = new FileWriter(configFile);
			prop.store(writer, "HTTP server settings");
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getPort() {
		int serverPort = Integer.parseInt(prop.getProperty("PORT"));
		if (serverPort <= 0 || serverPort > 65535) {
			setDefault("PORT");
			serverPort = DEFAULT_PORT;
		}
		return serverPort;
	}
	
	public int getThreadCount() {
		int threadCount = Integer.parseInt(prop.getProperty("THREAD_COUNT"));
		if (threadCount <= 0) {
			setDefault("THREAD_COUNT");
			threadCount = DEFAULT_THREAD_COUNT;
		}
		return threadCount;
	}
	
	public String getRootDir() {
		String rootDir = prop.getProperty("ROOT_DIR");
		if (rootDir == null) {
			File path = new File(DEFAULT_ROOT);
			if(!Files.exists(Paths.get(DEFAULT_ROOT))) {
				path.mkdir();
			}
			setDefault("ROOT_DIR");
			return DEFAULT_ROOT;
		}
		File path = new File(rootDir);
		if(!Files.exists(Paths.get(rootDir))) {
			path.mkdir();
		}
		return rootDir;
	}
	
}
