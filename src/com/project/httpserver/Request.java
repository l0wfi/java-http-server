package com.project.httpserver;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Request {
	
	private String requestLine;
	private HashMap<String, String> headers;
    private String method;
    private String target;
    private String version;
    private String GETParams;
    private String POSTParams;
    
    public Request(String requestLine, ArrayList<String> headers) {
    	this.requestLine = requestLine;
    	parseRequestLine();
    	this.headers = parseHeaders(headers);
    }
    
    public Request(String requestLine, ArrayList<String> headers, String POSTParams) {
    	this(requestLine, headers);
		this.POSTParams = POSTParams;
    }
    
    private HashMap<String, String>  extractParameters(String parameters) {
    	// firstname=mario&lastname=rossi
    	if(parameters == null || parameters.isEmpty()) {
    		return null;
    	}
    	HashMap<String, String> contentParams = new HashMap<String, String>();
    	String[] paramsArray = parameters.split("&");
    	// ["firstname=mario", "lastname=rossi"]
    	for(String param : paramsArray) {
    		String[] pair = param.split("=");
    		String value = pair.length > 1 ? pair[1] : null;
    		// ["firstname", "mario"]
    		contentParams.put(pair[0], value);
    	}
    	return contentParams;
    }
    
    private void parseRequestLine() {
    	System.out.println(requestLine);
    	String[] elements = requestLine.split("\\s");
		method = elements[0];
		target = elements[1];
		if (elements[1].contains("?")) {
			target = elements[1].split("\\?", 2)[0];
			GETParams = elements[1].split("\\?", 2)[1];
		}
		if (target.equals("/")) {
			target = "/index.html";
		}
		version = elements[2];
    }
    
    private HashMap<String, String> parseHeaders(ArrayList<String> headers) {
    	HashMap<String, String> headerFields = new HashMap<String, String>();
    	for(String field : headers) {
    		headerFields.put(field.split(": ", 2)[0], field.split(": ", 2)[1]);
    	}
    	return headerFields;
    }

    public String getMIMEType() {
    	String extension = target.split("\\.")[1];
		if (extension.equals("htm") || extension.equals("html")) {
			return "text/html";
		} else if (extension.equals("png") || extension.equals("gif")
				|| extension.equals("jpg") || extension.equals("jpeg")) {
			return "image/" + extension;
		}
		return null;
    }
    
	public String getRequestLine() {
		return requestLine;
	}

	public String getMethod() {
		return method;
	}

	public String getTarget() {
		return target;
	}

	public String getVersion() {
		return version;
	}

	public HashMap<String, String> getGETParams() {
		return extractParameters(GETParams);
	}
	
	public HashMap<String, String> getPOSTParams() {
		return extractParameters(POSTParams);
	}
    
}
