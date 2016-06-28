package com.ff.model;

import com.ff.vo.CountryVO;

public class AdvanceSearchReq {

	private int userProfole;
	
	private String sessionToken;
	private int pageNo;
	private String [] levelOfEdu;
	private String [] courses;
	private int durationStart;
	private int durationEnd;
	private int costStart;
	private int costEnd;
	private CountryVO[] locationList;
	
	
	public String[] getLevelOfEdu() {
		return levelOfEdu;
	}
	public void setLevelOfEdu(String[] levelOfEdu) {
		this.levelOfEdu = levelOfEdu;
	}
	public String[] getCourses() {
		return courses;
	}
	public void setCourses(String[] courses) {
		this.courses = courses;
	}
	public int getDurationStart() {
		return durationStart;
	}
	public void setDurationStart(int durationStart) {
		this.durationStart = durationStart;
	}
	public int getDurationEnd() {
		return durationEnd;
	}
	public void setDurationEnd(int durationEnd) {
		this.durationEnd = durationEnd;
	}
	public int getCostStart() {
		return costStart;
	}
	public void setCostStart(int costStart) {
		this.costStart = costStart;
	}
	public int getCostEnd() {
		return costEnd;
	}
	public void setCostEnd(int costEnd) {
		this.costEnd = costEnd;
	}

	public CountryVO[] getLocationList() {
		return locationList;
	}
	public void setLocationList(CountryVO[] locationList) {
		this.locationList = locationList;
	}
	public String getSessionToken() {
		return sessionToken;
	}
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getUserProfole() {
		return userProfole;
	}
	public void setUserProfole(int userProfole) {
		this.userProfole = userProfole;
	}
	
	
	
}
