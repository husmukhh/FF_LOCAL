package com.ff.model;

public class Course {

	private int courseId;
	private String courseTitle;
	private String country;
	private String durationType; 
	private String durationTime;
	private String costRange;
	private String recognition;
	private String recognitionType;
	
	private String avgWorldRank;
	private String countryImageUrl;
	private String remarks;
	
	private String sponsered = "N";
	
	private SponserDetails sponserDetails;
	
	
	public SponserDetails getSponserDetails() {
		return sponserDetails;
	}
	public void setSponserDetails(SponserDetails sponserDetails) {
		this.sponserDetails = sponserDetails;
	}
	
	public String getSponsered() {
		return sponsered;
	}
	public void setSponsered(String sponsered) {
		this.sponsered = sponsered;
	}
	
	
	
	
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public String getDurationType() {
		return durationType;
	}
	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}
	public String getDurationTime() {
		return durationTime;
	}
	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}
	public String getCostRange() {
		return costRange;
	}
	public void setCostRange(String costRange) {
		this.costRange = costRange;
	}
	public String getRecognition() {
		return recognition;
	}
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	public String getRecognitionType() {
		return recognitionType;
	}
	public void setRecognitionType(String recognitionType) {
		this.recognitionType = recognitionType;
	}
	public String getAvgWorldRank() {
		return avgWorldRank;
	}
	public void setAvgWorldRank(String avgWorldRank) {
		this.avgWorldRank = avgWorldRank;
	}
	public String getCountryImageUrl() {
		return countryImageUrl;
	}
	public void setCountryImageUrl(String countryImageUrl) {
		this.countryImageUrl = countryImageUrl;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
	
}
