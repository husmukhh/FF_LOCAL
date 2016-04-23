package com.ff.service.resp;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.ff.model.School;

public class SchoolResposeList {
	
	@JsonProperty("schoolList")
	private List<School> schoolList = new ArrayList<School>();

	public List<School> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(List<School> schoolList) {
		this.schoolList = schoolList;
	}


		
	
	
}
