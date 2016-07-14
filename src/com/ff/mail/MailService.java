package com.ff.mail;

public interface MailService {

	public void sendUserActivationMail(String user,String to,String sessionKey);
}
