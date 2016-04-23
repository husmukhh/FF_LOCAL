package com.ff.model;

public class UserInfo implements ResponseData {
	String fullName;
	String skypeId;
	String mobileNo;
	String gender;
	String coutOfOrign;
	String citizenship;
	String sessionId;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSkypeId() {
		return skypeId;
	}
	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCoutOfOrign() {
		return coutOfOrign;
	}
	public void setCoutOfOrign(String coutOfOrign) {
		this.coutOfOrign = coutOfOrign;
	}
	public String getCitizenship() {
		return citizenship;
	}
	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	
	
	
}
