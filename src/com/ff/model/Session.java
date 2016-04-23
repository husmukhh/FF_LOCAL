package com.ff.model;

public class Session {
	private String sessionToken;
	
	private String status;
	private String message ;
	private ResponseData data;
	
	public String getSessionToken() {
		return sessionToken;
	}
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
/*	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}*/
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ResponseData getData() {
		return data;
	}
	public void setData(ResponseData data) {
		this.data = data;
	}

	
	
	
	
}
