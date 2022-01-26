package com.project.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
	
	private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") +
			"/.JHttpServer/config/server.properties";
	public static final String HTTP_VERSION = "HTTP/1.1";
	
	private static int serverPort;
	private static int serverThreadCount;
	public static String serverRootDir;
	private static ConfigurationManager conf = new ConfigurationManager(DEFAULT_CONFIG_PATH);
	
	private ServerSocket welcomingSocket;
	private static Socket connectionSocket;
	
	private static ExecutorService pool;
	private static boolean run = true;
	
	private static myLogger myLogger;
	private static Logger logger;
	
	
	//Server and FixedThreadPool creation
	public static void main(String[] args) {
		Server serv = new Server();
		pool = Executors.newFixedThreadPool(serverThreadCount);
		serv.listen();
		
	}
	
	//Logger initialization, extraction of configuration parameters
	public Server() {
		myLogger = new myLogger();
		logger = myLogger.getLogger();
		
		serverPort = conf.getPort();
		serverThreadCount = conf.getThreadCount();
		serverRootDir = conf.getRootDir();
		
		logger.info(String.format("Listening: PORT %d, THREADS %d, ROOT DIR %s",
				serverPort, serverThreadCount, serverRootDir));
	}
	
	//Listen for new connections on the welcoming socket
	public void listen() {
		try {
			welcomingSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Welcoming socket error", e.getCause());
			e.printStackTrace();
		}
		
		while (run) {
			try {
				connectionSocket = welcomingSocket.accept();
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Welcoming socket accept error", e.getCause());
				e.printStackTrace();
			}
			//Execute a RequestHandler with a thread from the pool
			pool.execute(new RequestHandler(connectionSocket));
		}
		try {
			welcomingSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
