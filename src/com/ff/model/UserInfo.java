package com.ff.model;

import java.util.Date;

public class UserInfo implements ResponseData {
	String firstName;
	String lastName;
	
	String skypeId;
	String mobileNo;
	String gender;
	String coutOfOrign;
	String citizenship;
	String sessionId;
	String dob;
	
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String fullName) {
		this.firstName = fullName;
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
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}




	
	
	
}
