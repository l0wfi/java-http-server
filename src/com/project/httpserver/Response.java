package com.project.httpserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Response {
	
	private static final String CRLF = "\r\n";
	
	private String method;
	private String target;
	private String version;
	private String mimeType;
	private HashMap<String, String> GETParams;
	private HashMap<String, String> POSTParams;
	private boolean unsupportedMethod;
	
	private String[] statusCode;
	private long contentLength;
	private String statusLine;
	private String entityHeaders;
	
	public Response(Request req) {
		this.method = req.getMethod();
		this.target = req.getTarget();
		this.version = req.getVersion();
		this.mimeType = req.getMIMEType();
		this.GETParams = req.getGETParams();
		this.POSTParams = req.getPOSTParams();
	}
	
	public void sendTo(DataOutputStream out) throws IOException {
		statusCode = findStatusCode();
		createStatusLine();
		System.out.println(statusLine);
		out.writeBytes(statusLine);
		
		if (statusCode[0].equals("200")) {
			if (contentLength != -1) {
				entityHeaders = CRLF + "Content-Length: " + contentLength;
				entityHeaders += CRLF + "Content-Type: " + mimeType;
			}
		} else {
			// If the page content send is an error page
		}
		
		System.out.println(entityHeaders);
		out.writeBytes(entityHeaders);
		
		if (statusCode[0].equals("200")) {
			try {
				FileInputStream fis = new FileInputStream(Server.serverRootDir + target);
				
				out.writeBytes(CRLF + CRLF);
				
				byte[] buffer = new byte[1024];
		        int bytes = 0;
		
		        while ((bytes = fis.read(buffer)) != -1) {
		            out.write(buffer, 0, bytes);
		        }
		        
		        fis.close();
			} catch (Exception e) {
				// Send error 500
			}
		}
	}
	
	private boolean supportedMethod() {
		if (method.equals("GET") || method.equals("POST")) {
			return true;
		}
		return false;
	}
	
	private String[] findStatusCode() {
		if (!supportedMethod()) {
			return new String[] {"501" ,"Not Implemented"};
			
		} else if (!new File(Server.serverRootDir + target).exists()) {
			return new String[] {"404", "Not Found"};
			
		}
		File targetFile = new File(Server.serverRootDir + target);
		contentLength = -1;
		if(targetFile.exists())
			contentLength = targetFile.length();
		return new String[] {"200", "OK"};
	}
	
	private void createStatusLine() {
		statusLine = "HTTP/1.1" + " ";
		statusLine += statusCode[0] + " ";
		statusLine += statusCode[1];
	}
	
}
