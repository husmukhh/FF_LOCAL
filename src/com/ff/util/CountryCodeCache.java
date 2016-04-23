package com.ff.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.dao.DBUtil;
import com.ff.dao.SQLSelectQueries;
import com.ff.service.CourseServiceImpl;


public class CountryCodeCache {
	static Logger logger = LoggerFactory.getLogger(CountryCodeCache.class);
	public static final String DEFAULT_COUNTRY_CODE = "00";
	private static  Map<String,String> countryCodeMap = null ;
	
/*	static {
		load();
	}*/
	
	
	private static void load(){
		Statement statement = null;
		countryCodeMap = new HashMap<String,String>(132);
		try {
			logger.debug("INSIDE LOAD METHOD  : ");
			DBUtil dbUtil = new DBUtil();
			//Connection con = dbUtil.getJNDIConnection();
			Connection con = dbUtil.getJNDIConnection();
			statement = con.createStatement();
			
			logger.debug("load country cache load() statement created...");
			ResultSet result = statement.executeQuery(SQLSelectQueries.SELECT_COUNTRY_CODE_CACHE);
			
			logger.debug("Query executed ....");
			while(result.next()){
				logger.debug( "Key :" +   result.getString(1)  + " |  value : " + result.getString(2));
				countryCodeMap.put(result.getString(2),result.getString(1) );
			}
			
		} catch (SQLException e) {
			logger.error("inside CountryCodeCache load() method : ",e); 
		}finally {
			if(statement != null){
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	public static String getCountryName (String countryCode){
		logger.debug( " GET COUNTRY CODE :");
		if(countryCodeMap == null){
			logger.debug( " CALLING LOAD :");
			load();
		}
		if(countryCodeMap != null){
			String countryName = countryCodeMap.get(countryCode);
			logger.debug(countryName);
			return countryName;
		}else{
			return DEFAULT_COUNTRY_CODE;
		}
	}
}
