package com.ff.dao;

import com.ff.model.Session;
import com.ff.model.User;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;
import com.ff.vo.UserProfileVO;

public interface UserDAO {
	
	
	public Session authanticateUser(User user);

	public boolean saveUserInfo(UserInfo userInfo) throws Exception;
	public boolean updateUserInterest(UserInterest usrInterest);

	public boolean updateUserEducation(UserEducation userEducation);
	
	public UserInfo getUserInfo(long userId);
	
	public Session registerUser(User user);

	public boolean logOutSession(String sessionToken);

	public UserProfileVO getUserProfile(String sessionId);


	public Session updateProfileStatus(Session sessionToken);
	
	public Session activateUser(User user);
	
	public User getUser(String email);

}
