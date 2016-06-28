package com.ff.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

	Logger logger = LoggerFactory.getLogger(CourseDAOImpl.class);
	private static DataSource datasource;
    public static Map<String,String> HSC_EDU_MAP = new HashMap<String,String>(20);

	public Connection getJNDIConnection(){
	  
	    
	    Connection result = null;
	    try {
	      Context initialContext = new InitialContext();
	      
	      
	      Context envCtx = (Context) initialContext.lookup("java:comp/env");
	       
	      if (datasource == null) {
	    	  envCtx = (Context) envCtx.lookup("jdbc");
	    	  datasource =  (DataSource)envCtx.lookup("FF_DS");
	    	  result = datasource.getConnection();
	    	  logger.debug("....Connected to DB....");
	      }
	      else {
	    	  result = datasource.getConnection();
	      }
	    }
	    catch ( NamingException ex ) {
	      ex.printStackTrace();
	    }
	    catch(SQLException ex){
	      ex.printStackTrace();
	    }
	    return result;
	  }


   public  Connection getStandAloneConnection(){
	   
	   try{
		   String url = "jdbc:mysql://localhost/fresh_future";
		   Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		   Connection conn = DriverManager.getConnection (url, "root", "root");
		   return conn;
	   }catch(Exception exe){
		   exe.printStackTrace();
	   }
	   
	   return null;
   }

	


}
