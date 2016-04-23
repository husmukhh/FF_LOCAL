package com.ff.model;

public class SponserDetails {

	private String sponserName;
	private String addText;
	private int addType;
	private int courseId;
	private int sponserId;
	private String sponserLogoUrl;
	
	
	public int getSponserId() {
		return sponserId;
	}
	public void setSponserId(int sponserId) {
		this.sponserId = sponserId;
	}
	public String getSponserName() {
		return sponserName;
	}
	public void setSponserName(String sponserName) {
		this.sponserName = sponserName;
	}
	public String getAddText() {
		return addText;
	}
	public void setAddText(String addText) {
		this.addText = addText;
	}
	public int getAddType() {
		return addType;
	}
	public void setAddType(int addType) {
		this.addType = addType;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public String getSponserLogoUrl() {
		return sponserLogoUrl;
	}
	public void setSponserLogoUrl(String sponserLogoUrl) {
		
		this.sponserLogoUrl = sponserLogoUrl;
	}
	
	
	
	
}
