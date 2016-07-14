package com.ff.vo;

import java.util.List;

import com.ff.model.ResponseData;

public class LoggedInDataVO  implements ResponseData{

	private List<EligibityStatus> eligbilityList ;
	private String eduLevel;
	public List<EligibityStatus> getEligbilityList() {
		return eligbilityList;
	}
	public void setEligbilityList(List<EligibityStatus> eligbilityList) {
		this.eligbilityList = eligbilityList;
	}
	public String getEduLevel() {
		return eduLevel;
	}
	public void setEduLevel(String eduLevel) {
		this.eduLevel = eduLevel;
	}
	
	
}
