package com.ff.model;

public class UserEducation  implements ResponseData{
	
	private String eduCountry; 
	private String eduSystem; 
	private String eduSystemScore;
	private String eduInst;
	private String gpaScore;
	private String sessionId;
	
	private Subjects cambrigeSubGrds;
	
	private String isEngMedium; 
	private String ieltsToffel;
	
	private Subjects ieltsToffelScore;
	
	public String getEduCountry() {
		return eduCountry;
	}
	public void setEduCountry(String eduCountry) {
		this.eduCountry = eduCountry;
	}
	public String getEduSystem() {
		return eduSystem;
	}
	public void setEduSystem(String eduSystem) {
		this.eduSystem = eduSystem;
	}
	public String getEduInst() {
		return eduInst;
	}
	public void setEduInst(String eduInst) {
		this.eduInst = eduInst;
	}
	public String getGpaScore() {
		return gpaScore;
	}
	public void setGpaScore(String gpaScore) {
		this.gpaScore = gpaScore;
	}

	public String getIsEngMedium() {
		return isEngMedium;
	}
	public void setIsEngMedium(String isEngMedium) {
		this.isEngMedium = isEngMedium;
	}
	public String getIeltsToffel() {
		return ieltsToffel;
	}
	public void setIeltsToffel(String ieltsToffel) {
		this.ieltsToffel = ieltsToffel;
	}

	public Subjects getCambrigeSubGrds() {
		return cambrigeSubGrds;
	}
	public void setCambrigeSubGrds(Subjects cambrigeSubGrds) {
		this.cambrigeSubGrds = cambrigeSubGrds;
	}
	public Subjects getIeltsToffelScore() {
		return ieltsToffelScore;
	}
	public void setIeltsToffelScore(Subjects ieltsToffelScore) {
		this.ieltsToffelScore = ieltsToffelScore;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getEduSystemScore() {
		return eduSystemScore;
	}
	public void setEduSystemScore(String eduSystemScore) {
		this.eduSystemScore = eduSystemScore;
	}
	
	
	
}
