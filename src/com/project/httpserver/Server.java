package com.project.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
	
	private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") +
			"/.JHttpServer/config/server.properties";
	
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
	
	public static void main(String[] args) {
		Server serv = new Server();
		pool = Executors.newFixedThreadPool(serverThreadCount);
		serv.listen();
		
	}
	
	public Server() {
		myLogger = new myLogger();
		logger = myLogger.getLogger();
		
		logger.info("Loading server parameters from config file");
		serverPort = conf.getPort();
		serverThreadCount = conf.getThreadCount();
		serverRootDir = conf.getRootDir();
		
		logger.info(String.format("Starting server: port %d, threads %d, root directory %s",
				serverPort, serverThreadCount, serverRootDir));
	}
	
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
			
			logger.info(String.format("Client connected on port %d with address %s",
					connectionSocket.getPort(),
					connectionSocket.getInetAddress().toString().replace("/", "")));
			pool.execute(new RequestHandler(connectionSocket));
		}
		try {
			welcomingSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
