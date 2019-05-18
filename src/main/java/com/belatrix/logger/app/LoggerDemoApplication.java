package com.belatrix.logger.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggerDemoApplication.class, args);
		Map mp = new HashMap();
		mp.put("userName", "sa");
		mp.put("password", "");
		mp.put("dbms", "h2:mem:testdb");
		mp.put("serverName", "localhost");
		mp.put("portNumber", "8080");
		mp.put("logFileFolder", "./");
		
		JobLogger jb = new JobLogger(false, false, true, true, true, true, mp);
		try {
			jb.LogMessage(" mensaje prueba", true, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
