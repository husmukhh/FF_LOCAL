package com.ff.vo;

public class SearchVO {
	private String searchText ;  
	private String countryCode ; 
	private String[] courseTypes;
	private String sessionId ;
	private int pageNo;
	
	
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public String[] getCourseTypes() {
		return courseTypes;
	}
	public void setCourseTypes(String[] courseTypes) {
		this.courseTypes = courseTypes;
	}
	
	
	
}
