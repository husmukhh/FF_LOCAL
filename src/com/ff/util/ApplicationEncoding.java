package com.ff.util;

import org.apache.commons.codec.binary.Base64;

public class ApplicationEncoding {
	
	public static String encodeText(String text){
		byte[] encoded = Base64.encodeBase64(text.getBytes());
		return new String(encoded);
	}
	
	public static String decodeText(String encoded){
		 byte[] decoded = Base64.decodeBase64(encoded);      
		 return new String(decoded);
	}
	
}
