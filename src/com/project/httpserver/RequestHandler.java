package com.project.httpserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.accessibility.AccessibleExtendedTable;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.xml.crypto.Data;

public class RequestHandler implements Runnable {
	
	private static final String CRLF = "\r\n";
	private Socket socket;
	private BufferedReader in;
	private DataOutputStream out;
	
	private static myLogger myLogger;
	private static Logger logger;
	
	public RequestHandler(Socket socket) {
		this.socket = socket;
		myLogger = new myLogger();
		logger = myLogger.getLogger();
	}
	
	public void run() {
		try {
			logger.info("Handling request");
			handleRequest();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void handleRequest() throws Exception {

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new DataOutputStream(socket.getOutputStream());

		String startLine = in.readLine();
		
		String headers = "";
		while (in.ready()) {
			headers += in.readLine();
		}
		
		System.out.println(headers);

		logger.info("Request received, parsing");
		
		String[] requestParams = startLine.split(" ");
		String method = requestParams[0];
		String target = requestParams[1];
		if (target.equals("/")) {
			target = "/index.html";
		}
		
		logger.info(String.format("Request target: %s", target));
		logger.info("Sending HTTP response");
		
		sendResponse(target);
		
		in.close();
		out.close();
	}
	
	private void sendResponse(String targetName) throws Exception{
		String statusLine = null;
		String entityHeader = null;
		String messageBody = null;
		
		String targetPath = Server.serverRootDir + targetName;
		File targetFile = new File(targetPath);
		
		if(targetFile.exists()) {
			statusLine = "HTTP/1.1 200 OK" + CRLF;
			entityHeader = "Content-type: " + getContentType(targetName) + CRLF;
			
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            entityHeader = "Content-type: text/html" + CRLF;
            messageBody = "<html>" +
                    	    "<head>" +
                    		  "<title>Errore 404</title>" +
                    		"</head>" +
                    		"<body>404 Not Found</body>" +
                    	  "</html>";
		}
		
		out.writeBytes(statusLine);
		out.writeBytes(entityHeader);
		out.writeBytes(CRLF);
		
		if (messageBody == null) { 
			logger.info("Sending message body");
			FileInputStream fis = new FileInputStream(targetFile);
			byte[] buffer = new byte[1024];
	        int bytes = 0;
	
	        while ((bytes = fis.read(buffer)) != -1) {
	            out.write(buffer, 0, bytes);
	        }
	        fis.close();
		} else {
			logger.info("Sending Error 404");
			out.writeBytes(messageBody);
		}
	}
	
	private String getContentType(String targetName) {
		String extension = targetName.split("\\.")[1];
		
		if (extension == "html" || extension == "html") {
			return "text/html";
		} else if (extension == "png" || extension == "gif"
				|| extension == "jpg" || extension == "jpeg") {
			return "image/" + extension;
		}
		return null;
	}
}
