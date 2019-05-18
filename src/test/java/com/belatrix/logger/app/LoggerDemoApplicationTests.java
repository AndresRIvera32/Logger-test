package com.belatrix.logger.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Sql("/esquema.sql")
//@AutoConfigureTestDatabase
public class LoggerDemoApplicationTests {
	
	private Map mp;
	private static Logger loggerConsole = Logger.getLogger("MyLogFile");
	private static OutputStream logCapturingStream;
	private static StreamHandler customLogHandler;
	private PrintStream previousConsole;
	private ByteArrayOutputStream newConsole;
	private File f1;
	private Scanner sc;
	
	
	@Before
	public void beforeAll() throws ScriptException, SQLException{
		
		// Default Parameters
		mp = new HashMap();
		mp.put("userName", "sa");
		mp.put("password", "");
		mp.put("dbms", "h2:mem:testdb");
		mp.put("serverName", "localhost");
		mp.put("portNumber", "8080");
		mp.put("logFileFolder", "./");
		
		// Console Test
		previousConsole = System.out;		 
        newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));      
        
        //File Test
        sc = new Scanner(System.in);
        
	}
	

	@Test
	public void ConsoleTest() {
		
		JobLogger jb = new JobLogger(false, true, false, true, true, true, mp);
		String messageText = " message console test";
		try {
			jb.LogMessage(messageText, false, false, true);
			System.setOut(previousConsole);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		assertTrue(newConsole.toString().contains(messageText));
	}
	
	@Test
	public void FileTest(){
		JobLogger jb = new JobLogger(true, false, false, true, true, true, mp);
		String messageText = " message file test";
		String line;    
        boolean isFound = false;
		try {
			jb.LogMessage(messageText, false, false, true);
			f1 = new File(mp.get("logFileFolder") + "/logFile.txt");
			sc = new Scanner(f1);
	        block:while (sc.hasNext()) { 
                line = sc.nextLine();
                if (line.contains(messageText)) {
                	isFound = true;
                    break block;
                }
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.toString());
		} finally {
            if (sc != null) {
                sc.close();
            }
        }
		assertTrue(isFound);

	}
	
	@Test
	public void BDTest(){
		JobLogger jb = new JobLogger(false, false, true, true, true, true, mp);
		String messageText = " message BD test";
		try {
			jb.LogMessage(messageText, false, false, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(jb.isStored);
	}
	
	


}
