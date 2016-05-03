package com.ff.model;

import java.util.List;

public class AdvanceSearchReq {

	private String [] levelOfEdu;
	private String [] courses;
	private int durationStart;
	private int durationEnd;
	private int costStart;
	private int costEnd;
	private List<Location> locationList;
	
	
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
	public List<Location> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	
	
	
}
