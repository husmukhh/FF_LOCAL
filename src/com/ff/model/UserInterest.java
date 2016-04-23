package com.ff.model;

public class UserInterest  implements ResponseData{
	private String [] careerIntrests;
	private String [] countryIntrests;
	private String [] hobbies;
	private String jobRole;
	private String sessionId;
	
	public String[] getCareerIntrests() {
		return careerIntrests;
	}
	public void setCareerIntrests(String[] careerIntrests) {
		this.careerIntrests = careerIntrests;
	}
	
	public String[] getCountryIntrests() {
		return countryIntrests;
	}
	public void setCountryIntrests(String[] countryIntrests) {
		this.countryIntrests = countryIntrests;
	}

	
	
	public String[] getHobbies() {
		return hobbies;
	}


	public void setHobbies(String[] hobbies) {
		this.hobbies = hobbies;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getJobRole() {
		return jobRole;
	}
	public void setJobRole(String jobRole) {
		this.jobRole = jobRole;
	}
	
	
	
}
