package com.ff.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonUtil {
	 public static final String  STATUS_SUCCESS = "SUCCESS";
	 public static final String  CODE_SUCCESS = "200";
	
	
	public static String[] parseStringArrayFromJSONArray(String jsonArray)
	{
	    
		ObjectMapper mapper = new ObjectMapper();
		 String stringArray[]= null;
		try {
			stringArray = mapper.readValue(jsonArray, TypeFactory.defaultInstance().constructArrayType(String.class));
		} catch (IOException e) {
		}
		
	    return stringArray;
	}
	
	
}
