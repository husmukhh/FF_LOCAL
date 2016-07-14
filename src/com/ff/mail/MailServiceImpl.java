package com.ff.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.ff.util.ApplicationEncoding;

public class MailServiceImpl  extends Thread implements MailService{
	Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
	JavaMailSender jMailSender;
	private String to;
	private String subject;
	private String content;
	
	public JavaMailSender getjMailSender() {
		return jMailSender;
	}

	public void setjMailSender(JavaMailSender jMailSender) {
		this.jMailSender = jMailSender;
	}

	@Override
	public void sendUserActivationMail(String user, String to, String sessionKey) {
		String mail = "Dear "+user+", <br><br>Thank you for joining seeka. <br><br> <b><a href='"+getLink(user,to,sessionKey)+"'> Activate </a></b> your seeka account. <br><br><br> Thank You,<br> <b> Seeka Team </b>";
		this.to = to;
		this.subject = "Seeka - User Activation";
		this.content = mail;
		this.start();
		 //Thread thread = new Thread(new MailServiceImpl());
		//return sendMail(to, "Seeka - User Activation", mail);
	}

	private String getLink(String user,String to, String sessionKey){
		StringBuilder url = new StringBuilder();
		
		url.append("http://")
			.append("ec2-52-74-92-131.ap-southeast-1.compute.amazonaws.com")
			.append(":8080/FF_WS/activate.jsp?")
			.append("p1=").append(sessionKey).append("&")
			.append("p2=").append(ApplicationEncoding.encodeText(user)).append("&")
			.append("p3=").append(ApplicationEncoding.encodeText(to));
				
		return new String(url);
	}

	@Override
	public void run() {
		 MimeMessage message = jMailSender.createMimeMessage();
		   try{
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setFrom("Seeka<noreply@seekadegree.com>");
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(content,true);
		     }catch (MessagingException e) {
		    	 e.printStackTrace();
		     }
		     jMailSender.send(message);
	}
	
}
