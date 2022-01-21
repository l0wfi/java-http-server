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
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.accessibility.AccessibleExtendedTable;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.xml.crypto.Data;

public class RequestHandler implements Runnable {
	
	private Socket socket;
	private BufferedReader in;
	private DataOutputStream out;
	
	private Request clientReq;
	private Response serverResp;
	private int contentLength = -1;
	
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

		String requestLine = in.readLine();
		
		ArrayList<String> headers = new ArrayList<String>();
		
		String line = "";
		while ((line = in.readLine()) != null && (line.length() != 0)) {
			if(line.contains("Content-Length:")) {
				contentLength = Integer.parseInt(line.split(": ")[1]);
			}
			headers.add(line);
		}
		
		String postContent = "";
		if(contentLength != -1) {
			// char array with size of content
            char [] contentArray = new char[contentLength];
            // read bytes from BufferedReader and store in array
            in.read(contentArray, 0, contentLength);
            // convert char array to string
            postContent = new String(contentArray);
		}
		
		System.out.println(headers);
		
		logger.info("Request received, parsing");
		
		if (postContent.isEmpty()) {
			clientReq = new Request(requestLine, headers);
		}
		else {
			clientReq = new Request(requestLine, headers, postContent); 
		}
		
		logger.info("Sending response");
		serverResp = new Response(clientReq);
		serverResp.sendTo(out);
		
		in.close();
		out.close();
	}
	
}
