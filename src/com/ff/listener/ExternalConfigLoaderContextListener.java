package com.ff.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalConfigLoaderContextListener implements ServletContextListener{
	private static final Logger logger = LoggerFactory.getLogger(ExternalConfigLoaderContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String configLocation = sce.getServletContext().getInitParameter("CONFIGDIR");
		if(configLocation == null){
			configLocation = System.getenv("CONFIGDIR"); 
		}
		
		try{
			new LogBackConfigLoader(configLocation + "logback.xml");
		}catch(Exception e){
			logger.error("Unable to read config file", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {		
	}
}
