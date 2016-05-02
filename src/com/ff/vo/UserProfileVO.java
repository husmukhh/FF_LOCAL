package com.ff.vo;

import com.ff.model.UserDetails;

public class UserProfileVO {

	String message;
	UserDetails data;
	String status;
	public UserDetails getData() {
		return data;
	}
	public void setData(UserDetails data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
