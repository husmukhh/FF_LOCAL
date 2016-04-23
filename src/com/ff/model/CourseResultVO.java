package com.ff.model;

public class CourseResultVO implements ResponseData{

	private CourseDetails data ;
	private String status;
	private String message ;
	
	public CourseDetails getData() {
		return data;
	}
	public void setData(CourseDetails data) {
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
