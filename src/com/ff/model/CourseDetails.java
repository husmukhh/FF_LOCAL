package com.ff.model;

public class CourseDetails extends Course{

	private String university;
	private String city;
	private String courseTitle;
	private String intFees;
	private String currency;
	private String currencyTime;
	private String costSavings;
	private String twinningProgram;
	private String faculty;
	private String courseType;
	private String website;
	private String worldRanking;
	
	
	

	
	private UniversityInfo  universityInfo = new  UniversityInfo();
	

	
	
	
	

	public String getUniversity() {
		return university;
	}
	public void setUniversity(String university) {
		this.university = university;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public String getIntFees() {
		return intFees;
	}
	public void setIntFees(String intFees) {
		this.intFees = intFees;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCurrencyTime() {
		return currencyTime;
	}
	public void setCurrencyTime(String currencyTime) {
		this.currencyTime = currencyTime;
	}
	public String getCostSavings() {
		return costSavings;
	}
	public void setCostSavings(String costSavings) {
		this.costSavings = costSavings;
	}
	public String getTwinningProgram() {
		return twinningProgram;
	}
	public void setTwinningProgram(String twinningProgram) {
		this.twinningProgram = twinningProgram;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getWorldRanking() {
		return worldRanking;
	}
	public void setWorldRanking(String worldRanking) {
		this.worldRanking = worldRanking;
	}
	

	public UniversityInfo getUniversityInfo() {
		return universityInfo;
	}



	
	
	
}
