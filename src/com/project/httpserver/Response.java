package com.project.httpserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Logger;


public class Response {
	
	private static final String CRLF = "\r\n";
	
	private String ip;
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
	private String responseHeaders;
	
	private static myLogger myLogger;
	private static Logger logger;
	
	private String defaultErrorMessage =
			"<!DOCTYPE html>"
			+"<html>"
			+	"<head>"
			+		"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">"
			+		"<title>Error $CODE</title>"
			+	"</head>"
			+	"<body style=\"text-align:center\">"
			+		"<h1>$CODE</h1>"
			+		"<p>$MESSAGE</p>"
			+		"<hr>"
			+	"</body>"
			+"</html>";
	
	public Response(Request req) {
		myLogger = new myLogger();
		logger = myLogger.getLogger();
		
		this.ip = req.getIpAddr();
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
		out.write(statusLine.getBytes());
		
		responseHeaders = CRLF + "Date: " + getDate();
		
		String errorPage = "";
		if (statusCode[0].equals("200")) {
			if (contentLength != -1) {
				entityHeaders = CRLF + "Content-Length: " + contentLength;
				entityHeaders += CRLF + "Content-Type: " + mimeType;
			}
			
		} else {
			errorPage = createErrorPage(statusCode[0], statusCode[1]);
			entityHeaders = CRLF + "Content-Length: " + errorPage.getBytes("UTF-8").length;
		}
		
		out.write(responseHeaders.getBytes());
		out.write(entityHeaders.getBytes());
		
		if (!method.equals("HEAD")) {
			if (statusCode[0].equals("200")) {
				try {
					writeTargetTo(out);
					
				} catch (Exception e) {
					out.writeBytes(CRLF + CRLF);
					statusCode = new String[] {"500", "Internal server error"};
					out.writeBytes(createErrorPage("500", "Internal server error"));
				}
				
			} else {
				out.writeBytes(CRLF + CRLF);
				out.writeBytes(errorPage);
			}
		} else {
			out.writeBytes(CRLF + CRLF);
		}
		
		logResponse();
	}
	
	private boolean supportedMethod() {
		if (method.equals("GET") || method.equals("POST") || method.equals("HEAD")) {
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
	
	private String createErrorPage(String code, String message) {
		return defaultErrorMessage.replace("$CODE", code)
				.replace("$MESSAGE", message);
	}
	
	private void writeTargetTo(DataOutputStream out) throws Exception {
		FileInputStream fis = new FileInputStream(Server.serverRootDir + target);
		
		out.writeBytes(CRLF + CRLF);
		
		byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
        }
        
        fis.close();
	}
	
	//https://www.rfc-editor.org/rfc/rfc2616#section-14.18
	private String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
		return dtf.format(ZonedDateTime.now(ZoneOffset.UTC));
	}
	
	private void logResponse() {
		logger.info(String.format("%s - \"%s %s %s\" - %s %s",
				ip, method, target, version,
				statusCode[0], statusCode[1]));
	}
	
}
