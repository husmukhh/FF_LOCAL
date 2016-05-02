package com.ff.vo;

import java.util.ArrayList;
import java.util.List;

import com.ff.model.Course;
import com.ff.model.ResponseData;

public class SearchResultVO implements ResponseData {
	private List<Course> data = new ArrayList<Course>();
	private int pageNo;
	private String status;
	private String message;
	private int totalRecords;
	
	public List<Course> getData() {
		return data;
	}

	public void setData(List<Course> data) {
		this.data = data;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	
	
}
