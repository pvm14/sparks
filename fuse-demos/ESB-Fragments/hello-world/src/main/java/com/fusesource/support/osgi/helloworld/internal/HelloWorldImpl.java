package com.fusesource.support.osgi.helloworld.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.fusesource.support.osgi.helloworld.HelloWorld;


public class HelloWorldImpl implements HelloWorld, InitializingBean, DisposableBean {
	
	public static final Logger logger = LoggerFactory.getLogger(HelloWorldImpl.class);
	
	private String response; 
	private String contrib;
	
	
	public HelloWorldImpl() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("Fragment");
			contrib = rb.getString("RESPONSE");			
		} catch(MissingResourceException mre) {
			logger.info("No fragement detected ..");
		} catch(Exception e) {
			logger.error("Exception: " + e.getMessage());
		}
	}
	
	
	public String sayHello(String message) {
		logger.info("Just got message '" + message + "'");
		logger.info("Returning... '" + response + "'");
		return response;
	}

	public void setResponse(String response) {
		logger.info("Setting the response to '" + response + "'");
		this.response = response;
	}

	public void afterPropertiesSet() throws Exception {
		logger.info("HelloWorldImpl initialized.");
		logger.info("Will return response '" + response + "'");
		logger.info("Contributed (via Fragment) is '" + contrib + "'");
	}

	public void destroy() throws Exception {
		logger.info("Shutting down HelloWorldImpl");
	}
	
}
