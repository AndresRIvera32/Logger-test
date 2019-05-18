package com.belatrix.logger.app;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private static Map dbParams;
	private static Logger logger;
	public boolean isStored;



	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLogFile");  
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}

	public void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {

		if (messageText == null || messageText.trim().length() == 0) {
			return;
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		int t = 0;
		String l = null;
		Level log = Level.INFO;
		File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		
		FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
		ConsoleHandler ch = new ConsoleHandler();
		
		
		if (error && logError) {
			l = "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;	
			log = Level.SEVERE;
			t = 2;
		}

		if (warning && logWarning) {
			l = "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
			log = Level.WARNING;
			t = 3;
		}

		if (message && logMessage) {
			l = "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
			log = Level.INFO;
			t = 1;
		}

		if(logToFile) {
			logger.addHandler(fh);
			logger.log(log, l);
		}
		
		if(logToConsole) {
			logger.addHandler(ch);
			logger.log(log, l);
		}

		if(logToDatabase) {
			conectDataBase(l,t);
		}
	}
	
	
	public void conectDataBase(String l, int t) {
		Connection connection = null;
		Statement stmt = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", dbParams.get("userName"));
		connectionProps.put("password", dbParams.get("password"));
		try {
			connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
					+ ":" + dbParams.get("portNumber") + "/", connectionProps);
			stmt = connection.createStatement();	
			// line for junit test
			stmt.execute("create table IF NOT EXISTS LOG_VALUES (tipo INTEGER, mensaje varchar(60));");
			stmt.executeUpdate("insert into LOG_VALUES values(" + t + ", '" + l + "')");
			isStored = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isStored = false;
		}finally{
		      
		      try{
		         if(stmt!=null)
		        	 connection.close();
		      }catch(SQLException se){
		    	  se.printStackTrace();
		      }
		      try{
		         if(connection!=null)
		        	connection.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		}
	}
	
}


