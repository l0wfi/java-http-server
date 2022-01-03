package com.project.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class myLogger {
	private static myLogger myLogger;
    private static Logger logger;    
    private static FileHandler fh;
    private static SimpleFormatter sf;
    
    public myLogger() {
    	if (myLogger != null) {
            return;
        }
    	checkLogDir("./logs");
		try {
			// Create a FileHandler on the log file and set append to true
			fh = new FileHandler("./logs/server.log", true);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

        logger = Logger.getLogger("myLogger");
        sf = new SimpleFormatter();
        fh.setFormatter(sf);            
        logger.addHandler(fh);

        myLogger = this;
    }
    
    public Logger getLogger() {
    	return myLogger.logger;
    }
    
    public void checkLogDir(String dir) {
    	File path = new File(dir);
		if(!Files.exists(Paths.get(dir))) {
			path.mkdir();
		}
    }
}
