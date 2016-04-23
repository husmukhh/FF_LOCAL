package com.ff.service;

import javax.ws.rs.core.Response;

import com.ff.dao.UserDAO;
import com.ff.model.Session;
import com.ff.model.User;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;
import com.ff.model.UserProfileVO;

public class UserServiceImpl implements UserService {
	
	
	
	UserDAO userDao;
	
	@Override
	public Response login(User user) {
		
		Session session = userDao.authanticateUser(user);
		return Response.ok(session).build();
	}

	@Override
	public Response userInfo( UserInfo userInfo	) {
		boolean isUpdated = userDao.saveUserInfo(userInfo);
		Session session = new Session();
		if(isUpdated == true){
			session.setData(userInfo);
			session.setMessage("Your personal information are successfully updated.");
			session.setSessionToken(userInfo.getSessionId());
			session.setStatus("OK");
		}else{
			session.setMessage("Some technical issue occured.Unable to store your information.Please try later.");
			session.setSessionToken(userInfo.getSessionId());
			session.setStatus("FAIL");
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
			session.setStatus("OK");
		}else{
			session.setMessage("Some technical issue occured.Unable to store your information.Please try later.");
			session.setSessionToken(userInterest.getSessionId());
			session.setStatus("FAIL");
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
			session.setStatus("OK");
		}else{
			session.setMessage("Unable to update user profile. Please try later.");
			session.setStatus("FAIL");
		}
		return Response.ok(session).build();
	}

	
	
	
	
	@Override
	public Response logOut(Session sessionToken) {
		Session session = new Session();
		
		boolean isLogout = userDao.logOutSession(sessionToken.getSessionToken());
		
		if(isLogout){
			session.setMessage("Successfully logged out.");
			session.setStatus("OK");
		}else{
			session.setMessage("Unable to logout. Please try to logout later.");
			session.setStatus("FAIL");
		}
		return Response.ok(session).build();
	}

	@Override
	public Response registration(User user) {
		Session session = userDao.registerUser( user);
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


	
	
}