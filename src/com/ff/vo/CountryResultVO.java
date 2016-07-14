package com.ff.vo;

import com.ff.model.CountryDetails;
import com.ff.model.ResponseData;

public class CountryResultVO implements ResponseData {

	private CountryDetails data;
	private String status;
	private String message ;
	
	public CountryDetails getData() {
		return data;
	}

	public void setData(CountryDetails data) {
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
