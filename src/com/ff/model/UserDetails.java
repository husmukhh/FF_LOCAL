package com.ff.model;

import java.util.List;

import com.ff.vo.EligibityStatus;

public class UserDetails {



	
/*	private float read;
	private float write;
	private float speak;
	private float listen;
	private float overall;*/
	
	private UserInterest userInterest = new UserInterest();
	
	private String email;

	private UserEducation userEducation = new UserEducation();
	private UserInfo userInfo = new UserInfo();;
	private List<EligibityStatus> userSearchEligibility;
	
	public UserEducation getUserEducation() {
		return userEducation;
	}
	public void setUserEducation(UserEducation userEducation) {
		this.userEducation = userEducation;
	}
/*	public String getIeltsToeffl() {
		return ieltsToeffl;
	}
	public void setIeltsToeffl(String ieltsToeffl) {
		this.ieltsToeffl = ieltsToeffl;
	}
*//*	public float getRead() {
		return read;
	}
	public void setRead(float read) {
		this.read = read;
	}
	public float getWrite() {
		return write;
	}
	public void setWrite(float write) {
		this.write = write;
	}
	public float getSpeak() {
		return speak;
	}
	public void setSpeak(float speak) {
		this.speak = speak;
	}
	public float getListen() {
		return listen;
	}
	public void setListen(float listen) {
		this.listen = listen;
	}
	public float getOverall() {
		return overall;
	}
	public void setOverall(float overall) {
		this.overall = overall;
	}*/

	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public UserInterest getUserInterest() {
		return userInterest;
	}
	public void setUserInterest(UserInterest userInterest) {
		this.userInterest = userInterest;
	}
	public List<EligibityStatus> getUserSearchEligibility() {
		return userSearchEligibility;
	}
	public void setUserSearchEligibility(List<EligibityStatus> userSearchEligibility) {
		this.userSearchEligibility = userSearchEligibility;
	}

	

	
	
	
}
