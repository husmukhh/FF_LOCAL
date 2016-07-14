package com.ff.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ff.util.ApplicationEncoding;

public class MailServiceImpl implements MailService{
	Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
	JavaMailSender jMailSender;
	public JavaMailSender getjMailSender() {
		return jMailSender;
	}

	public void setjMailSender(JavaMailSender jMailSender) {
		this.jMailSender = jMailSender;
	}

	@Override
	public boolean sendUserActivationMail(String user, String to, String sessionKey) {
		String mail = "Dear "+user+", <br><br>Thank you for joining seeka. <br><br> <b><a href='"+getLink(user,to,sessionKey)+"'> Activate </a></b> your seeka account. <br><br><br> Thank You,<br> <b> Seeka Team </b>";
		return sendMail(to, "Seeka - User Activation", mail);
	}

	private boolean sendMail(String to, String subject, String content) {
		   MimeMessage message = jMailSender.createMimeMessage();
		   try{
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setFrom("Seeka<noreply@seekadegree.com>");
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(content,true);
		     }catch (MessagingException e) {
		    	 e.printStackTrace();
		    	 return false;
		     }
		     jMailSender.send(message);
		     return true;
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
	
	
	
}
