package com.ff.service;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ff.dao.UserDAO;

import javax.ws.rs.core.Response;

import com.ff.dao.UserDAO;
import com.ff.mail.MailService;
import com.ff.model.AppConstants;

import com.ff.model.Session;
import com.ff.model.User;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;

import com.ff.util.ApplicationEncoding;

import com.ff.vo.UserProfileVO;

public class UserServiceImpl implements UserService {
	

	UserDAO userDao;
	MailService jMailService;
	

	
	@Override
	public Response login(User user) {
		
		Session session = userDao.authanticateUser(user);
		return Response.ok(session).build();
	}

	@Override
	public Response userInfo( UserInfo userInfo	) {
		Session session = new Session();
		
		boolean isUpdated;
		try {
			isUpdated = userDao.saveUserInfo(userInfo);
			if(isUpdated == true){
				session.setData(userInfo);
				session.setMessage("Your personal information are successfully updated.");
				session.setSessionToken(userInfo.getSessionId());
				session.setStatus("1");
			}else{
				session.setMessage("Some technical issue occured.Unable to store your information.Please try later.");
				session.setSessionToken(userInfo.getSessionId());
				session.setStatus("0");
			}
			
		}catch(java.text.ParseException parseExe){
			session.setMessage("Invalid date format. Format is dd/mm/yyyy");
			session.setSessionToken(userInfo.getSessionId());
			session.setStatus("0");			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return Response.ok(session).build();
	}


	@Override
	public Response userInterests(UserInterest userInterest ) {
		boolean isUpdated = userDao.updateUserInterest(userInterest);

		Session session = new Session();
		session.setSessionToken(userInterest.getSessionId());
		if(isUpdated == true){
			session.setData(userInterest);
			session.setMessage("Your intrests are successfully updated.");
			session.setSessionToken(userInterest.getSessionId());
			session.setStatus("1");
		}else{
			session.setMessage("Some technical issue occured.Unable to store your information.Please try later.");
			session.setSessionToken(userInterest.getSessionId());
			session.setStatus("0");
		}
		return Response.ok(session).build();
	}


	@Override
	public Response userEducation(UserEducation userEducation ) {
		Session session = new Session();
		
		boolean isUpdated = userDao.updateUserEducation(userEducation);
		
		if(isUpdated){
			session.setData(userEducation);
			session.setMessage("Education updated successfully.");
			session.setSessionToken(userEducation.getSessionId());
			session.setStatus("1");
		}else{
			session.setMessage("Unable to update user profile. Please try later.");
			session.setStatus("0");
		}
		return Response.ok(session).build();
	}

	
	
	
	
	@Override
	public Response logOut(Session sessionToken) {
		Session session = new Session();
		
		boolean isLogout = userDao.logOutSession(sessionToken.getSessionToken());
		
		if(isLogout){
			session.setMessage("Successfully logged out.");
			session.setStatus("1");
		}else{
			session.setMessage("Unable to logout. Please try to logout later.");
			session.setStatus("0");
		}
		return Response.ok(session).build();
	}

	@Override
	public Response registration(User user) {
		Session session = userDao.registerUser( user);

		if(Integer.parseInt(session.getStatus()) == AppConstants.SUCCESS){
			jMailService.sendUserActivationMail(user.getUserName(),user.getEmail(), session.getSessionToken());
		}

		return Response.ok(session).build();
	}

	public UserDAO getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	@Override
	public Response getUserProfile(Session sessionToken) {
		UserProfileVO userProfileVO = userDao.getUserProfile(sessionToken.getSessionToken());
		
		
		return Response.ok(userProfileVO).build();
	}


	@Override
	public Response updateUserProfileStatus(Session sessionToken) {
		
		Session session = userDao.updateProfileStatus(sessionToken);
		return Response.ok(session).build();
	}
	
	public MailService getjMailService() {
		return jMailService;
	}

	public void setjMailService(MailService jMailService) {
		this.jMailService = jMailService;
	}

	@Override
	public Session activateAccount(String p1,String p2, String p3){
		User  user = new User();
		user.setUserName(ApplicationEncoding.decodeText(p2));
		user.setEmail(ApplicationEncoding.decodeText(p3));
		Session session = new Session();
		session.setSessionToken(p1);
		user.setSession(session);
		return userDao.activateUser(user);

	}



}
