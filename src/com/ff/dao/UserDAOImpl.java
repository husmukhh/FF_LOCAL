package com.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.model.AppConstants;
import com.ff.model.Session;
import com.ff.model.User;
import com.ff.model.UserDetails;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;
import com.ff.model.UserProfileVO;

public class UserDAOImpl implements UserDAO {

	

	Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
	
	DBUtil dbUtil ;
	@Override
	public Session authanticateUser(User user){
		
		Connection con = dbUtil.getJNDIConnection(); 
		Session session = new Session();
		PreparedStatement statement = null;
		PreparedStatement sessionStatement = null;
		
		try{
			if(con != null){

				con.setAutoCommit(false);
				
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_WHERE);
				statement.setString(1, user.getUserName());
				statement.setString(2, user.getPassword());
				logger.debug(" SQL : "+statement.toString());
				ResultSet result = statement.executeQuery();
				String sessionToken = null ;
				while(result.next()){
					user.setUserId(result.getLong("id"));
					sessionToken = result.getString("session_token");
					
					if(sessionToken != null && !sessionToken.isEmpty()){
						logOutSession(sessionToken);
					}					
				}
				

				sessionStatement = con.prepareStatement(SQLInsertQuries.SESSION_INSERT);
				sessionStatement.setLong(1, user.getUserId());
				
				logger.debug(" SQL : "+sessionStatement.toString());
				int rowCount = sessionStatement.executeUpdate();
				sessionStatement.close();
				con.commit();
				
				sessionStatement = con.prepareStatement(SQLSelectQueries.SESSION_SELECT);
				sessionStatement.setLong(1, user.getUserId());
				logger.debug(" SQL : "+sessionStatement.toString());
				result = sessionStatement.executeQuery();
				
				while(result.next()){
					session.setSessionToken(result.getString(1));
				}
				session.setMessage("Successfully logged in.");
				
				session.setStatus("OK");
				con.commit();
				//session.setUserId(user.getUserId());
				
			}else{
				session.setStatus("FAIL");
				session.setMessage("Invalid Username or Password,unbale to login. Please try again.");
				return session;
			}
			
			
		}catch(SQLException exe){
			logger.error(" authanticateUser() : " , exe);
		}finally{
			try {
				if(statement != null){
				statement.close();
				}
				if(sessionStatement != null){
				sessionStatement.close();
				}
				//closeConnection(con);
			} catch (SQLException e) {
				
				logger.error(" finnaly :  authanticateUser() : " , e);
				return session;
			}
		}
		return session;
		
	}
	
	
	private Session authanticateUser(String userName , String password , Connection con){
		Session session = new Session();
		PreparedStatement statement = null;
		PreparedStatement sessionStatement = null;
		
		try{
			if(con != null){
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_WHERE);
				statement.setString(1, userName);
				statement.setString(2, password);
				logger.debug(" SQL : "+statement.toString());
				ResultSet result = statement.executeQuery();
				User user = new User();
				
				while(result.next()){
					user.setUserId(result.getLong("id"));
				}
				
				sessionStatement = con.prepareStatement(SQLInsertQuries.SESSION_INSERT);
				sessionStatement.setLong(1, user.getUserId());
				logger.debug(" SQL : "+sessionStatement.toString());
				
				int rowCount = sessionStatement.executeUpdate();
				
				con.commit();
				sessionStatement.close();
				sessionStatement = con.prepareStatement(SQLSelectQueries.SESSION_SELECT);
				sessionStatement.setLong(1, user.getUserId());
				logger.debug(" SQL : "+sessionStatement.toString());
				result = sessionStatement.executeQuery();
				
				while(result.next()){
					session.setSessionToken(result.getString(1));
				}
				
				session.setStatus("OK");
				session.setMessage("Thank you for your registration. Welcome to SEEKA.");
				
			}else{
				session.setMessage("Some problem occuered in connecting to database. Please try later or contact with system adminstrator.");
				return session;
			}
			
			
		}catch(SQLException exe){
			logger.error("private authanticateUser() :" , exe);
		}finally{
			try {
				if(statement != null){
				statement.close();
				}
				if(sessionStatement != null){
				sessionStatement.close();
				}
				
				//closeConnection(con);
				
			} catch (SQLException e) {
				logger.error("finally : private authanticateUser() :" , e);
				return session;
			}
		}
		return session;
		
	}
	
	
	
	@Override
	public boolean saveUserInfo(UserInfo userInfo) {
		Connection con = dbUtil.getJNDIConnection(); 
		PreparedStatement userInfoStatement = null;
		PreparedStatement deleteExistingStatement = null;
		long userId = -1;
		try{
			userId = getUserIdBySession(userInfo.getSessionId(), con);
			if(userId > 0){
			//con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			con.setAutoCommit(false);
			deleteExistingStatement = con.prepareStatement(SQLDeleteQuries.USER_INFO_DELETE);
			deleteExistingStatement.setLong(1, userId);
			logger.debug(" SQL : "+deleteExistingStatement.toString());
			int recordAffected = deleteExistingStatement.executeUpdate();
			con.commit();

			
			userInfoStatement = con.prepareStatement(SQLInsertQuries.USER_INFO_INSERT);
			userInfoStatement.setLong(1,userId);
			userInfoStatement.setString(2, userInfo.getFullName());
			userInfoStatement.setString(3, userInfo.getSkypeId());
			userInfoStatement.setString(4, userInfo.getMobileNo());
			userInfoStatement.setString(5, userInfo.getGender());
			userInfoStatement.setString(6, userInfo.getCoutOfOrign());
			userInfoStatement.setString(7, userInfo.getCitizenship());
			logger.debug(" SQL : "+userInfoStatement.toString());
			int rowCountUserInfo = userInfoStatement.executeUpdate();

			con.commit();
			return true;
			}else{
				return false;
			}
		}catch(Exception exe){
			logger.error("saveUserInfo() : ",exe);
			return false;	

		}finally{
			try {
				if(userId > 0){
					deleteExistingStatement.close();
					userInfoStatement.close();
				}
				//closeConnection(con);				
			}catch (SQLException e) {

				logger.error("finally : saveUserInfo() : ",e);
			} 
			catch (Exception e) {

				logger.error("finally : saveUserInfo() : ",e);
		
			}
		}
		
	}


	public long getUserId(String username) throws Exception{
		Connection con = dbUtil.getJNDIConnection();
		PreparedStatement statement = null;
		long userId = -1L;
		try{
			if(con != null){
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_ID_WHERE);
				statement.setString(1, username);
				logger.debug(" SQL : "+statement.toString());
				ResultSet result = statement.executeQuery();
				while(result.next()){
					userId = result.getLong(1);
				}				
			}else{
				logger.error("Throw error : getUserId , connection is null.");
				throw new Exception ("Error while checking duplicate username");
			}

		}catch(SQLException exe){

				logger.error("getUserId()" , exe);
		}finally{
			try {
				closeStatement(statement);
				
				//closeConnection(con);
			} catch (SQLException e) {

				logger.error("finally : getUserId : ",e);
			}
		}
		return userId;
	}


	@Override
	public boolean updateUserEducation(UserEducation userEducation) {
		Connection con = dbUtil.getJNDIConnection();
		PreparedStatement userEduStatement = null;
		PreparedStatement userEduAOLevelStatement = null;
		PreparedStatement userEduIelTofStatement = null;
		long userId = -1;
		try{
		con.setAutoCommit(false);
		userId = getUserIdBySession(userEducation.getSessionId(),con);

		if(userId > 0){
			
			deleteUserProfile(userId,SQLDeleteQuries.USER_EDU_DELETE,con);
			deleteUserProfile(userId,SQLDeleteQuries.USER_EDU_IELTS_TOFFEL_DELETE,con);
			deleteUserProfile(userId,SQLDeleteQuries.USER_EDU_A_O_DELETE,con);			
			
			userEduStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_INSERT);
			userEduStatement.setLong(1, userId);
			userEduStatement.setString(2, userEducation.getEduCountry());
			userEduStatement.setString(3, userEducation.getEduSystem());
			userEduStatement.setString(4, userEducation.getEduSystemScore());
			userEduStatement.setString(5, userEducation.getEduInst());
			userEduStatement.setString(6, userEducation.getGpaScore());
			userEduStatement.setString(7, userEducation.getIsEngMedium());
			userEduStatement.setString(8, userEducation.getIeltsToffel());
			int rowCountUser = userEduStatement.executeUpdate();
			
			if(userEducation.getEduSystem().equals(AppConstants.CAMBRIDGE)){
				for(String subject : userEducation.getCambrigeSubGrds().getSubjects().keySet()){
					userEduAOLevelStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_A_O_SCORE_INSERT);
					userEduAOLevelStatement.setString(2, subject);
					userEduAOLevelStatement.setLong(1,userId);
					userEduAOLevelStatement.setString(3,(String)userEducation.getCambrigeSubGrds().getSubjects().get(subject));
					userEduAOLevelStatement.executeUpdate();
					userEduAOLevelStatement.close();
				}
			}
			
			if(userEducation.getIeltsToffel().equals("Y")){
				for(String subject : userEducation.getIeltsToffelScore().getSubjects().keySet()){
					userEduIelTofStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_A_O_SCORE_INSERT);
					userEduIelTofStatement.setString(2, subject);
					userEduIelTofStatement.setLong(1,userId);
					userEduIelTofStatement.setString(3,(String)userEducation.getCambrigeSubGrds().getSubjects().get(subject));
					userEduIelTofStatement.executeUpdate();
					userEduIelTofStatement.close();
				}
			}			
			con.commit();
			return true;
		}else{
			return false;
		}
			
		}catch(Exception exe){
			logger.error("updateUserEducation()" , exe);
			return false;	
		}finally{
			try {
				if(userId > 0){
				userEduStatement.close();
				}
				//closeConnection(con);				
			} catch (SQLException e) {
				logger.error("finally getUserEducation()" , e);
				return false;	
			}
		}
		
	}


	@Override
	public boolean updateUserInterest(UserInterest usrInterest) {
		Connection con = dbUtil.getJNDIConnection();
		PreparedStatement userHobbyStatement = null;
		PreparedStatement userCareerStatement = null;
		PreparedStatement userCountryStatement = null;
	try{
		con.setAutoCommit(false);
		long userId = getUserIdBySession(usrInterest.getSessionId(),con);
		deleteUserProfile(userId,SQLDeleteQuries.USER_INTEREST_CAREER_DELETE , con);
		deleteUserProfile(userId,SQLDeleteQuries.USER_INTEREST_HOBBY_DELETE , con);
		deleteUserProfile(userId,SQLDeleteQuries.USER_INTEREST_COUNTRY_DELETE , con);
		

		
		userHobbyStatement = con.prepareStatement(SQLInsertQuries.USER_HOBBY_INTEREST);
		saveValues(userHobbyStatement , userId, usrInterest.getHobbies());
		
		userCareerStatement = con.prepareStatement(SQLInsertQuries.USER_CAREER_INTEREST);
		userCareerStatement.setString(3, usrInterest.getJobRole());
		saveValues(userCareerStatement , userId, usrInterest.getCareerIntrests());

		userCountryStatement = con.prepareStatement(SQLInsertQuries.USER_COUNTRIES_INTREST);
		saveValues(userCountryStatement , userId, usrInterest.getCareerIntrests());

		con.commit();
		return true;	
		}catch(SQLException exe){
			logger.error("updateUserInterest() : " , exe);
			return false;
		}finally{
			try {
				closeStatement(userHobbyStatement);
				closeStatement(userCountryStatement);
				closeStatement(userCareerStatement);
			} catch (SQLException e) {
				logger.error("finally : updateUserInterest() : ",e); 
			}
		}		
	}

	private void saveValues(PreparedStatement statement, long userId, String[] valuesArray) throws SQLException{
		for(String value : valuesArray){
			statement.setLong(1, userId);
			statement.setString(2, value);
			statement.addBatch();
		}
		logger.debug(" SQL : "+statement.toString());
		statement.executeBatch();
	}


	private void deleteUserProfile(long userId, String query , Connection con) {
		PreparedStatement statement = null;
		try{
			statement= con.prepareStatement(query);
			statement.setLong(1, userId);
			logger.debug(" SQL : "+statement.toString());
			int result = statement.executeUpdate();
			con.commit();

		}catch(SQLException exe){
			logger.error("deleteUserProfile() : ", exe);
		}finally{
			try {
				closeStatement(statement);
				
				//closeConnection(con);				
			} catch (SQLException e) {
				logger.error("finally deleteUserProfile() : ", e);
			}
		}		
		
	}	
	
	private long getUserIdBySession(String sessionId, Connection con) {
		PreparedStatement statement = null;
		long userId = -1L;
		try{
			con.setAutoCommit(false);
			 statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_ID_BY_SESSION_WHERE);
			statement.setString(1, sessionId);
			logger.debug(" SQL : "+statement.toString());
			ResultSet result = statement.executeQuery();
			while(result.next()){
				userId = result.getLong(1);
			}
			con.commit();
		}catch(SQLException exe){
			logger.error("getUserIdBySession() : ", exe); 
		}finally{
			try {
				closeStatement(statement);
				//closeConnection(con);
				
			} catch (SQLException e) {
				logger.error("finally : getUserBySessionId() : ",e);
			}
		}		
		return userId;		
		
	}


	private void closeStatement(PreparedStatement statement) throws SQLException {
		if(statement != null)
		statement.close();
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}
	public void setDbUtil(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}


	@Override
	public UserInfo getUserInfo(long userId) {
		
		PreparedStatement statement = null;
		Connection con = dbUtil.getJNDIConnection();
		try{
			
			statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_ID_BY_SESSION_WHERE);
			statement.setLong(1, userId);
			logger.debug(" SQL : "+statement.toString());
			ResultSet result = statement.executeQuery();
			while(result.next()){
				
			}
			
		}catch(SQLException exe){
			logger.error("getUserInfo() : ",exe);
		}finally{
			try {
				closeStatement(statement);
				//closeConnection(con);
			} catch (SQLException e) {
				logger.error("finnally : getUserInfo() : ",e);
			}
		}		
		return null;
	}


	@Override
	public Session  registerUser(User user) {
		PreparedStatement statement = null;
		Session session = new Session();
		Connection con = null;
		try {
			 con = dbUtil.getJNDIConnection();
			 con.setAutoCommit(false);
			long existId = getUserId(user.getUserName());
			if(existId < 0){
				statement = con.prepareStatement(SQLInsertQuries.USER_INSERT);
				statement.setString(1, user.getUserName());
				statement.setString(2, user.getEmail());
				statement.setString(3, user.getPassword());
				logger.debug(" SQL : "+statement.toString());
				int rowAffected = statement.executeUpdate();
				con.commit();
				if(rowAffected > 0) {
					session  = authanticateUser(user.getUserName() , user.getPassword() , con);
				}
			}
		} catch (SQLException e) {
			session.setStatus("FAIL");
			session.setMessage("Unable to register user. Please try again later");

			logger.error("registerUser() : ",e);
		} catch (Exception e) {
			session.setStatus("FAIL");
			session.setMessage("Unable to register user. Please try again later");
			
			logger.error("registerUser() : ",e);
		}finally{
			
			try {
				closeStatement(statement);
				//closeConnection(con);
			} catch (SQLException e) {
				logger.error("finnaly registerUser() : ",e);
			}
		}

		return session;
	}


	@Override
	public boolean logOutSession(String sessionToken) {
		//USER_SESSION_DELETE
		PreparedStatement statement = null;
		Session session = new Session();
		Connection con = null;
		try {
			
			con = dbUtil.getJNDIConnection();
			con.setAutoCommit(false);
			statement = con.prepareStatement(SQLDeleteQuries.USER_SESSION_DELETE);
			statement.setString(1, sessionToken);
			logger.debug(" SQL : "+statement.toString());
			int rowAffected = statement.executeUpdate();
			con.commit();
			if(rowAffected > 0){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			session.setStatus("FAIL");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("logOutSession() : ",e);
		} catch (Exception e) {
			session.setStatus("FAIL");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("logOutSession() : ",e);
		}finally{
			
			try {
				if(statement != null)
				closeStatement(statement);
				//closeConnection(con);
			} catch (SQLException e) {
				logger.error("finally : logOutSession() : ",e);
			}
		}
		
		
		return false;
	}


	@Override
	public UserProfileVO getUserProfile(String sessionId) {
		
		 UserProfileVO userDetailVO = new UserProfileVO();		

	   	 PreparedStatement statement = null;
		 Connection con = null;
		
   	     String userProfileQuery = " SELECT " +
   	    	    " usrEd.edu_country, usrEd.edu_institue, usrEd.gpa_score, usrEd.edu_system, usrEd.edu_sys_score, " +
   	    	    " usrEd.toffel_ielts, usrInfo.full_name, usrInfo.skype_id, usrInfo.mobile_no, usrInfo.gender, " +
   	    	    " usrInfo.country_origin, usrInfo.citizenship, usr.email, usr_carer.career_txt , usr_carer.job_role" +
   	    		" FROM users usr " + 
   	    	    " left join   user_interest_carrer  usr_carer on usr_carer.user_id = usr.id " + 
   	    	    " left join   user_edu  usrEd on usrEd.user_id = usr.id "  +
   	    	    " left join   user_info  usrInfo on usrInfo.user_id = usr.id " + 
   	    		" WHERE usr.id = ?"  ; 
   	 
   	     UserDetails ud = null;
   	    
   	 try {
			
			con = dbUtil.getJNDIConnection();
			con.setAutoCommit(false);
			long userId = getUserIdBySession(sessionId, con);
			statement = con.prepareStatement(userProfileQuery);
			statement.setLong(1, userId);
			ResultSet resultSet = statement.executeQuery();
			logger.debug(" SQL : "+statement.toString());
			while(resultSet.next()){
				ud = new UserDetails( );
				ud.setEduCountry(resultSet.getString("edu_country"));
				ud.setEduInstitute(resultSet.getString("edu_institue"));
				ud.setGpaScore(resultSet.getString("gpa_score"));
				ud.setEduSystem(resultSet.getString("edu_system"));
				ud.setEduSysScore(resultSet.getString("edu_sys_score"));
				ud.setIeltsToeffl(resultSet.getString("toffel_ielts"));
				ud.setFullName(resultSet.getString("full_name"));
				ud.setSkypeId(resultSet.getString("skype_id"));
				ud.setMobileNo(resultSet.getString("mobile_no"));
				ud.setGender(resultSet.getString("gender"));
				ud.setCtryOrigin(resultSet.getString("country_origin"));
				ud.setCitizenship(resultSet.getString("citizenship"));
				ud.setEmail(resultSet.getString("email"));	
				ud.setCareerInterest(resultSet.getString("career_txt"));
			}	
			
			if(ud != null){
				if("Y".equals( ud.getIeltsToeffl() )){
					setUserLanguageScore(ud,userId,con);
				}
				
				getUserHobbies (ud , userId , con);
				userDetailVO.setData(ud);
				userDetailVO.setMessage("User profile successfully retrived.");
				userDetailVO.setStatus("OK");
			}	
			
		
			
	} catch (SQLException e) {
		userDetailVO.setData(ud);
		userDetailVO.setMessage("Some technical issue occured.Please try later.");
		userDetailVO.setStatus("FAIL");
		logger.error("getUserProfile()",e);
		} catch (Exception e) {

			logger.error("getUserProfile()",e);
		}finally{
			
			try {
				closeStatement(statement);
			} catch (SQLException e) {
				logger.error("finally : getUserProfile()",e);
			}
		}   	    
		return userDetailVO;
	}


	private void getUserHobbies(UserDetails ud, long userId, Connection con) {
		String userHobbiesQuery = " select hobby_txt from user_interest_hobby where user_id = ? ";
		List<String> hobbiesList = new ArrayList<String>();
		PreparedStatement userHobbyStm = null;
	   	try {
	   		userHobbyStm = con.prepareStatement(userHobbiesQuery);
	   		userHobbyStm.setLong(1, userId);
	   		ResultSet result = userHobbyStm.executeQuery();
	   		logger.debug(" SQL : "+userHobbyStm.toString());
	   		while(result.next()){
	   			hobbiesList.add(result.getString("hobby_txt"));
	   		}
	   		ud.setUserHobies(hobbiesList);
	   		
	   	}catch(Exception exe){
			logger.error("getUserHobbies()",exe);
	   	}finally{
			
			try {
				closeStatement(userHobbyStm);
			} catch (SQLException e) {
				logger.error("finally : getUserHobbies()",e);
			}
		}		
		
	}


	private void setUserLanguageScore(UserDetails ud, long userId, Connection con) {
		String userEngLangQuery = "select `read` , `write` , speak , listen, overall from user_edu_iel_tof_score where user_id = ?";
		PreparedStatement userIeltsTofStm = null;
	   	try {
	   		userIeltsTofStm = con.prepareStatement(userEngLangQuery);
	   		userIeltsTofStm.setLong(1, userId);
	   		logger.debug(" SQL : "+userIeltsTofStm.toString());
	   		ResultSet result = userIeltsTofStm.executeQuery();
	   		
	   		while(result.next()){
	   			ud.setRead(result.getFloat("read"));
	   			ud.setWrite(result.getFloat("write"));
	   			ud.setSpeak(result.getFloat("speak"));
	   			ud.setListen(result.getFloat("listen"));
	   			ud.setOverall(result.getFloat("overall"));
	   		}
	   	}catch(Exception exe){
			logger.error("setUserLanguageScore()",exe);
	   	}finally{
			
			try {
				closeStatement(userIeltsTofStm);
			} catch (SQLException e) {
				logger.error("setUserLanguageScore()",e);
			}
		}
		
	}
	
	
	
	
	
	
}
