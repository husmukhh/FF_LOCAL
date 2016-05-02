package com.ff.vo;

import com.ff.model.CourseDetails;
import com.ff.model.ResponseData;

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
