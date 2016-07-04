package com.ff.test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTester {

	
	public static void main(String arg[]) throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = "{\"subjects\" : {\"english\" : \"A+\",      \"math\" : \"B\",      \"physics\" : \"D+\"} }";

		
		

		// convert JSON string to Map
//		Subjects subject  = mapper.readValue(json,Subjects.class);
		

	//	System.out.println(subject.getSubjects());		
	}
	
	public String myJonTester(String json){
		
		return json;
	}
}


