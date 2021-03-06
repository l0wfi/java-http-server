package com.project.httpserver;

import java.util.ArrayList;
import java.util.HashMap;

public class Request {
	
	private String ip;
	private String requestLine;
	private HashMap<String, String> headers;
    private String method;
    private String target;
    private String version;
    private String GETParams;
    private String POSTParams;
    
    public Request(String ip, String requestLine, ArrayList<String> headers) {
    	this.ip = ip;
    	this.requestLine = requestLine;
    	parseRequestLine();
    	this.headers = parseHeaders(headers);
    }
    
    public Request(String ip, String requestLine, ArrayList<String> headers, String POSTParams) {
    	this(ip, requestLine, headers);
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
    	//Split request line at every whitespace
    	String[] elements = requestLine.split("\\s");
    	
		method = elements[0];
		target = elements[1];
		
		//If target contains GET parameters split the path and the parameters
		if (elements[1].contains("?")) {
			target = elements[1].split("\\?", 2)[0];
			GETParams = elements[1].split("\\?", 2)[1];
		}
		//If the target is the root
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
    	//Split filename at "." and get file extension
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
	
	public String getIpAddr() {
		return ip.replace("/", "");
	}
    
}
