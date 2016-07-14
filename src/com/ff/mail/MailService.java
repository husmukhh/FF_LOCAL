package com.ff.mail;

public interface MailService {

	public boolean sendUserActivationMail(String user,String to,String sessionKey);
}
