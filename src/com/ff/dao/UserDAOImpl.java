package com.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.model.AppConstants;
import com.ff.model.Session;
import com.ff.model.Subjects;
import com.ff.model.User;
import com.ff.model.UserDetails;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;
import com.ff.util.ApplicationConstant;
import com.ff.util.EducationSystemConstants;
import com.ff.vo.EligibityStatus;
import com.ff.vo.LoggedInDataVO;
import com.ff.vo.UserProfileVO;

public class UserDAOImpl implements UserDAO {

	

	Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
	private static final DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
	DBUtil dbUtil ;
	@Override
	public Session authanticateUser(User user){
		
		Connection con = dbUtil.getJNDIConnection(); 
		Session session = new Session();
		LoggedInDataVO data = new LoggedInDataVO();
		PreparedStatement statement = null;
		PreparedStatement sessionStatement = null;
		PreparedStatement usrEduLvlStatement = null;
		PreparedStatement eligbStatement = null;
		try{
			if(con != null){

				con.setAutoCommit(false);
				
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_ACTIVE);
				statement.setInt(1, AppConstants.ACTIVE);
				statement.setString(2, user.getUserName());
				logger.debug(" SQL [Check Active] : " + statement.toString());
				ResultSet resultActive = statement.executeQuery();
				if(!resultActive.next()){
					if(resultActive.getRow() == 0){
						session.setStatus("1");
						session.setMessage("User is not activated. Please check your email for activation.");
						return session;					
					}
				}
				
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_WHERE);
				statement.setString(1, user.getUserName());
				statement.setString(2, user.getPassword());
				logger.debug(" SQL : " + statement.toString());
				ResultSet result = statement.executeQuery();
				String sessionToken = null ;
				
				while(result.next()){
					user.setUserId(result.getLong("id"));
					sessionToken = result.getString("session_token");
					if(sessionToken != null && !sessionToken.isEmpty()){
						logOutSession(sessionToken);
					}					
				}
				
				if(user.getUserId() == null){
					session.setStatus("0");
					session.setMessage("Invalid Username or Password,unbale to login. Please try again.");
					return session;					
				}
				sessionStatement = con.prepareStatement(SQLInsertQuries.SESSION_INSERT);
				sessionStatement.setLong(1, user.getUserId());
				
				logger.debug(" SQL : " + sessionStatement.toString());
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
				
				String eduLevQuery = "select edu_level,edu_system from user_edu  where user_id = ? ";
				usrEduLvlStatement = con.prepareStatement(eduLevQuery);
				usrEduLvlStatement.setLong(1, user.getUserId());
				ResultSet usrEduLvlRes = usrEduLvlStatement.executeQuery();
				String userEduLevel = "";
				String userEduSys   = "";
				while(usrEduLvlRes.next()){
					userEduLevel = usrEduLvlRes.getString("edu_level");
					userEduSys = usrEduLvlRes.getString("edu_system");
				}

				String userEligibityQuery = "select  country_name , course_type ," + userEduSys + " from country_eligibility " ;
				List<EligibityStatus> eligibityStatusList = new ArrayList<>();
				
				try {
					eligbStatement =  con.prepareStatement(userEligibityQuery);
					logger.debug(" SQL : "+eligbStatement.toString());
					
			   		ResultSet eligibResult = eligbStatement.executeQuery();
			   		EligibityStatus eliStatus = new EligibityStatus();
			   		 if(eligibResult != null ){
			   			 while(eligibResult.next()){
			   				eliStatus.setCountryName(eligibResult.getString("country_name"));
			   				eliStatus.setCourseType(eligibResult.getString("course_type"));
			   				eliStatus.setStatus(eligibResult.getInt(userEduSys));
			   				eligibityStatusList.add(eliStatus);
			   				eliStatus = new EligibityStatus();
			   			 }
			   		 }
			   		
			   		data.setEduLevel(userEduLevel);
			   		data.setEligbilityList(eligibityStatusList);
			   		
			   		session.setData(data);
			   	}catch(Exception exce){
			   		logger.error("catch : authanticateUser : ",exce);
			   	}

				session.setMessage("Successfully logged in.");
				session.setStatus("1");
				con.commit();
				//session.setUserId(user.getUserId());
				
			}else{
				session.setStatus("0");
				session.setMessage("Unable to connect to database.Please contact with System Adminstrator.");
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
				if(usrEduLvlStatement != null){
				usrEduLvlStatement.close();
				}				
				closeConnection(con);
				if(eligbStatement != null){
					eligbStatement.close();
				}				
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
				
				session.setStatus("1");
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
	public boolean saveUserInfo(UserInfo userInfo) throws Exception{
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
			userInfoStatement.setString(2, userInfo.getFirstName());
			userInfoStatement.setString(3, userInfo.getLastName());
			if(userInfo.getDob() != null){
				java.sql.Date sqlDate = new java.sql.Date(formatter.parse(userInfo.getDob()).getTime() );
				userInfoStatement.setDate(4, sqlDate);
			}
			userInfoStatement.setString(5, userInfo.getSkypeId());
			userInfoStatement.setString(6, userInfo.getMobileNo());
			userInfoStatement.setString(7, userInfo.getGender());
			userInfoStatement.setString(8, userInfo.getCoutOfOrign());
			userInfoStatement.setString(9, userInfo.getCitizenship());
			logger.debug(" SQL : "+userInfoStatement.toString());
			userInfoStatement.executeUpdate();

			con.commit();
			return true;
			}else{
				return false;
			}
		}
		catch(java.text.ParseException parseExe){
			throw parseExe;
		}
		catch(Exception exe){
			logger.error("saveUserInfo() : ",exe);
			return false;	

		}finally{
			try {
				if(userId > 0){
					deleteExistingStatement.close();
					userInfoStatement.close();
				}
				closeConnection(con);				
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
				
				closeConnection(con);
			} catch (SQLException e) {

				logger.error("finally : getUserId : ",e);
			}
		}
		return userId;
	}
	public long getUserEmail(String email) throws Exception{
		Connection con = dbUtil.getJNDIConnection();
		PreparedStatement statement = null;
		long userId = -1L;
		try{
			if(con != null){
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_EMAIL_WHERE);
				statement.setString(1, email);
				logger.debug(" SQL : "+statement.toString());
				ResultSet result = statement.executeQuery();
				while(result.next()){
					userId = result.getLong(1);
				}				
			}else{
				logger.error("Throw error : getUserEmail , connection is null.");
				throw new Exception ("Error while checking duplicate user email");
			}

		}catch(SQLException exe){

				logger.error("getUserEmail()" , exe);
		}finally{
			try {
				closeStatement(statement);
				
				closeConnection(con);
			} catch (SQLException e) {

				logger.error("finally : getUserEmail : ",e);
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

		if(userId > 0 ){
			
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
			userEduStatement.setString(9, userEducation.getEduLevel());
			int rowCountUser = userEduStatement.executeUpdate();
			
			if( ( EducationSystemConstants.SRI_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.MAL_STPM.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SGP_A_LEVE.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.UK_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.GLOB_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SRI_O_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.MAL_SPM.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SGP_O_LEVE.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.UK_O_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.GLOBE_O_LEV.equals( userEducation.getEduSystem() )
				 )
					&& 
					
					userEducation.getCambrigeSubGrds() != null
					){
				for(String subject : userEducation.getCambrigeSubGrds().getSubjects().keySet()){
					userEduAOLevelStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_A_O_SCORE_INSERT);
					userEduAOLevelStatement.setString(2, subject);
					userEduAOLevelStatement.setLong(1,userId);
					userEduAOLevelStatement.setString(3,(String)userEducation.getCambrigeSubGrds().getSubjects().get(subject));
					userEduAOLevelStatement.executeUpdate();
					userEduAOLevelStatement.close();
				}
			}
			
/*			if(userEducation.getEduSystem().equals(AppConstants.CAMBRIDGE_O)){
				for(String subject : userEducation.getCambrigeSubGrds().getSubjects().keySet()){
					userEduAOLevelStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_A_O_SCORE_INSERT);
					userEduAOLevelStatement.setString(2, subject);
					userEduAOLevelStatement.setLong(1,userId);
					userEduAOLevelStatement.setString(3,(String)userEducation.getCambrigeSubGrds().getSubjects().get(subject));
					userEduAOLevelStatement.executeUpdate();
					userEduAOLevelStatement.close();
				}
			}*/			
			
			if(userEducation.getIeltsToffel().equals("TOFFEL") ||  userEducation.getIeltsToffel().equals("IELTS") && userEducation.getIeltsToffelScore() != null){
				Map<String,Object> subjectMap = userEducation.getIeltsToffelScore().getSubjects();
				userEduIelTofStatement = con.prepareStatement(SQLInsertQuries.USER_EDU_IELTS_TOFFEL_SCROE_INSERT);
				userEduIelTofStatement.setLong(1, userId);
				
				
				userEduIelTofStatement.setString(2,(String)subjectMap.get("read"));
				userEduIelTofStatement.setString(3,(String)subjectMap.get("write"));
				userEduIelTofStatement.setString(4,(String)subjectMap.get("speak"));
				userEduIelTofStatement.setString(5,(String)subjectMap.get("listen"));
				userEduIelTofStatement.setString(6,(String)subjectMap.get("overall"));

				
				
				userEduIelTofStatement.executeUpdate();
				userEduIelTofStatement.close();
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
				closeConnection(con);				
			} catch (SQLException e) {
				logger.error("finally getUserEducation()" , e);
				return false;	
			}
		}
		
	}


	private boolean validateUserEducation(UserEducation userEducation ) {
		if(userEducation != null 					  &&
				userEducation.getEduSystem() != null  && 
				!userEducation.getEduSystem().isEmpty() ){
			
			if(EducationSystemConstants.SRI_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.MAL_STPM.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SGP_A_LEVE.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.UK_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.GLOB_A_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SRI_O_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.MAL_SPM.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.SGP_O_LEVE.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.UK_O_LEV.equals( userEducation.getEduSystem() )
					|| EducationSystemConstants.GLOBE_O_LEV.equals( userEducation.getEduSystem() )
					
					
					){
				
				if(userEducation.getCambrigeSubGrds() != null){
					return true;
				}
			}else{
				
				if(userEducation.getEduSystemScore() != null ){
					return true;
				}else{
					
					return false;
				}
			}			
			
			
			
			
		}
		return false;
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
		saveValues(userCountryStatement , userId, usrInterest.getCountryIntrests());

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
				closeConnection(con);
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
				closeConnection(con);
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
				if(existId > 0){
					session.setStatus("0");
					session.setMessage("Username already registered.");
					return session;
				}
				
				long existEmail = getUserEmail(user.getEmail());
				if(existEmail > 0 ){
					session.setStatus("0");
					session.setMessage("Provided email is already registered.");
					return session;
				}
					
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
		} catch (SQLException e) {
			session.setStatus("0");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("registerUser() : ",e);
		} catch (Exception e) {
			session.setStatus("0");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("registerUser() : ",e);
		}finally{
			
			try {
				closeStatement(statement);
				closeConnection(con);
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
			session.setStatus("0");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("logOutSession() : ",e);
		} catch (Exception e) {
			session.setStatus("0");
			session.setMessage("Unable to register user. Please try again later");
			logger.error("logOutSession() : ",e);
		}finally{
			
			try {
				if(statement != null)
				closeStatement(statement);
				closeConnection(con);
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
   	    	    " usrEd.user_id, usrEd.edu_id, usrEd.edu_country, usrEd.edu_institue, usrEd.gpa_score, usrEd.edu_system, usrEd.edu_sys_score, " +
   	    	    " usrEd.toffel_ielts, usrEd.edu_level, usrInfo.first_name, usrInfo.last_name, usrInfo.dob,usrInfo.skype_id, usrInfo.mobile_no, usrInfo.gender, " +
   	    	    " usrInfo.country_origin, usrInfo.citizenship, usr.email " +
   	    		" FROM users usr " + 
   	    	    /*" left join   user_interest_carrer  usr_carer on usr_carer.user_id = usr.id " + */
   	    	    " left join   user_edu  usrEd on usrEd.user_id = usr.id "  +
   	    	    /*" left join   user_intrest_country  usrIntCount on usrIntCount.user_id = usr.id "  +*/
   	    	    " left join   user_info  usrInfo on usrInfo.user_id = usr.id " + 
   	    		" WHERE usr.id = ?"  ; 
   	 
   	     UserDetails ud = null;
   	    
   	 try {
			
			con = dbUtil.getJNDIConnection();
			con.setAutoCommit(false);
			long userId = getUserIdBySession(sessionId, con);
			
			if(userId > 0){
				statement = con.prepareStatement(userProfileQuery);
				statement.setLong(1, userId);
				ResultSet resultSet = statement.executeQuery();
				logger.debug(" SQL : "+statement.toString());
				while(resultSet.next()){
					ud = new UserDetails( );
					ud.getUserEducation().setEduId(resultSet.getInt("edu_id"));
					ud.getUserEducation().setEduCountry(resultSet.getString("edu_country"));
					ud.getUserEducation().setEduInst(resultSet.getString("edu_institue"));
					ud.getUserEducation().setGpaScore(resultSet.getString("gpa_score"));
					ud.getUserEducation().setEduSystem(resultSet.getString("edu_system"));
					ud.getUserEducation().setEduSystemScore(resultSet.getString("edu_sys_score"));
					ud.getUserEducation().setIeltsToffel(resultSet.getString("toffel_ielts"));
					ud.getUserEducation().setEduLevel(resultSet.getString("edu_level"));
					
					ud.getUserInfo().setFirstName(resultSet.getString("first_name"));
					ud.getUserInfo().setLastName(resultSet.getString("last_name"));
					if(resultSet.getDate("dob") != null)
					ud.getUserInfo().setDob(formatter.format( new Date(resultSet.getDate("dob").getTime())));
					ud.getUserInfo().setSkypeId(resultSet.getString("skype_id"));
					ud.getUserInfo().setMobileNo(resultSet.getString("mobile_no"));
					ud.getUserInfo().setGender(resultSet.getString("gender"));
					ud.getUserInfo().setCoutOfOrign(resultSet.getString("country_origin"));
					ud.getUserInfo().setCitizenship(resultSet.getString("citizenship"));
					ud.setEmail(resultSet.getString("email"));	
/*					ud.getUserInterest().setCareerIntrests(new String [] {resultSet.getString("career_txt") });
					ud.getUserInterest().setCountryIntrests(new String [] {resultSet.getString("country_code") });
					ud.getUserInterest().setJobRole(resultSet.getString("job_role"));*/
				}	
				
				if(ud != null && ud.getUserEducation() != null){
					if("TOEFL".equals( ud.getUserEducation().getIeltsToffel() ) ||  "IELTS".equals( ud.getUserEducation().getIeltsToffel())){
						setUserLanguageScore(ud,userId,con);
					}
					
					if (EducationSystemConstants.GLOB_A_LEV.equals( ud.getUserEducation().getEduSystem() ) ||
						EducationSystemConstants.GLOBE_O_LEV.equals(ud.getUserEducation().getEduSystem() ) ||
						EducationSystemConstants.SGP_A_LEVE.equals(ud.getUserEducation().getEduSystem())  ||
						EducationSystemConstants.SGP_O_LEVE.equals(ud.getUserEducation().getEduSystem() ) ||
						EducationSystemConstants.SRI_A_LEV.equals(ud.getUserEducation().getEduSystem()) ||
						EducationSystemConstants.SRI_O_LEV.equals(ud.getUserEducation().getEduSystem())
							){
						
						String subScore = "select * from user_edu_a_o where user_id = ? " ;
						PreparedStatement subScoreStatement = con.prepareStatement(subScore);
						subScoreStatement.setLong(1, userId);
						
						ResultSet subScoreRes = subScoreStatement.executeQuery();
						Subjects subjects = new Subjects ();
						while(subScoreRes.next()){
							subjects.getSubjects().put(subScoreRes.getString("sub_name"),subScoreRes.getString("grade") );
						}
						ud.getUserEducation().setCambrigeSubGrds(subjects);
						ud.getUserEducation().setIsEduAorO(1);
					}
					
					getUserHobbies (ud , userId , con);
					getUserInterestCareer(ud,userId,con);
					getUserInterestCountry(ud,userId,con);
					getUserSearchEligibity(ud,userId,con);
					userDetailVO.setData(ud);
					userDetailVO.setMessage("User profile successfully retrived.");
					userDetailVO.setStatus("1");
				}	
				
				
			}else{
				
				userDetailVO.setData(null);
				userDetailVO.setMessage("Invalid credentials.");
				userDetailVO.setStatus("0");				
			}
		
			
	} catch (SQLException e) {
		userDetailVO.setData(ud);
		userDetailVO.setMessage("Some technical issue occured.Please try later.");
		userDetailVO.setStatus("0");
		logger.error("getUserProfile()",e);
		} catch (Exception e) {

			logger.error("getUserProfile()",e);
		}finally{
			
			try {
				closeStatement(statement);
				closeConnection(con);
			} catch (SQLException e) {
				logger.error("finally : getUserProfile()",e);
			}
		}   	    
		return userDetailVO;
	}


	private void getUserInterestCountry(UserDetails ud, long userId, Connection con) {
		String usrCntryIntrQuery = " select country_code from user_intrest_country where user_id = ? ";
		List<String> countryIntrList = new ArrayList<String>();
		PreparedStatement userCntryIntrStmt = null;
	   	try {
	   		userCntryIntrStmt = con.prepareStatement(usrCntryIntrQuery);
	   		userCntryIntrStmt.setLong(1, userId);
	   		ResultSet result = userCntryIntrStmt.executeQuery();
	   		logger.debug(" SQL : "+userCntryIntrStmt.toString());
	   		while(result.next()){
	   			countryIntrList.add(result.getString("country_code"));
	   		}
	   		
	   		 String [] intrests = new String [countryIntrList.size()];
	   		 for(int i = 0 ; i < countryIntrList.size(); i++){
	   			intrests[i] = countryIntrList.get(i);
	   		}
	   		
	   		ud.getUserInterest().setCountryIntrests(intrests);
	   	}catch(Exception exe){
			logger.error("getUserInterestCountry()",exe);
	   	}finally{
			
			try {
				closeStatement(userCntryIntrStmt);
			} catch (SQLException e) {
				logger.error("finally : getUserInterestCountry()",e);
			}
		}		

		
	}


	private void getUserInterestCareer(UserDetails ud, long userId, Connection con) {
		String usrCareerIntrst = " select career_txt,job_role from user_interest_carrer where user_id = ? ";
		List<String> careerIntrstList = new ArrayList<String>();
		PreparedStatement usrCareerIntStmt = null;
	   	try {
	   		usrCareerIntStmt = con.prepareStatement(usrCareerIntrst);
	   		usrCareerIntStmt.setLong(1, userId);
	   		ResultSet result = usrCareerIntStmt.executeQuery();
	   		logger.debug(" SQL : "+usrCareerIntStmt.toString());
	   		while(result.next()){
	   			careerIntrstList.add(result.getString("career_txt"));
	   		}
	   		
	   		 String [] careerIntrests = new String [careerIntrstList.size()];
	   		 for(int i = 0 ; i < careerIntrstList.size(); i++){
	   			careerIntrests[i] = careerIntrstList.get(i);
	   		}
	   		
	   		ud.getUserInterest().setCareerIntrests(careerIntrests);
	   	}catch(Exception exe){
			logger.error("getUserInterestCareer()",exe);
	   	}finally{
			
			try {
				closeStatement(usrCareerIntStmt);
			} catch (SQLException e) {
				logger.error("finally : getUserInterestCareer()",e);
			}
		}		

		
	}



	private void getUserSearchEligibity(UserDetails ud, long userId, Connection con) {
		if(ud.getUserEducation() != null 
				&& ud.getUserEducation().getEduSystem() != null 
				&& !ud.getUserEducation().getEduSystem().isEmpty()){

			String userEligibityQuery = "select  country_name , course_type ," + ud.getUserEducation().getEduSystem() + " from country_eligibility " ;
			List<EligibityStatus> eligibityStatusList = new ArrayList<>();
			try {
				PreparedStatement statement =  con.prepareStatement(userEligibityQuery);
				logger.debug(" SQL : "+statement.toString());
				System.out.println(statement.toString());
		   		ResultSet result = statement.executeQuery();
		   		EligibityStatus eliStatus = new EligibityStatus();
		   		 if(result != null ){
		   			 while(result.next()){
		   				eliStatus.setCountryName(result.getString("country_name"));
		   				eliStatus.setCourseType(result.getString("course_type"));
		   				eliStatus.setStatus(result.getInt(ud.getUserEducation().getEduSystem()));
		   				eligibityStatusList.add(eliStatus);
		   				eliStatus = new EligibityStatus();
		   			 }
		   		 }
		   		ud.setUserSearchEligibility(eligibityStatusList);
		   	}catch(Exception exce){
		   		logger.error("catch : getUserSearchEligibity() : ",exce);
		   	}

		}
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
	   		
	   		 String [] hobbies = new String [hobbiesList.size()];
	   		 for(int i = 0 ; i < hobbiesList.size(); i++){
	   			hobbies[i] = hobbiesList.get(i);
	   		}
	   		
	   		ud.getUserInterest().setHobbies(hobbies);
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
	   		
	   		Subjects subject = new Subjects();
	   		
	   		if(result.next()){
	   		subject.getSubjects().put("read", result.getFloat("read"));
	   		subject.getSubjects().put("write" , result.getFloat("write"));
	   		subject.getSubjects().put("speak",result.getFloat("speak"));
	   		subject.getSubjects().put("listen",result.getFloat("listen"));
	   		subject.getSubjects().put("overall",result.getFloat("overall"));
	   		ud.getUserEducation().setIeltsToffelScore(subject);
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
	
	
	
	
	private void closeConnection(Connection con) {
		
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("close connection : " , e);
			}
		}
		
	}


	@Override
	public Session activateUser(User user) {
		PreparedStatement statement = null;
		Connection con = null;
		Session session = new Session();
		try {
			 con = dbUtil.getJNDIConnection();
			 con.setAutoCommit(false);
				long existId = getUserId(user.getUserName());
				if(existId > 0){
					session.setStatus("0");
					session.setMessage("Account activation link is not valid, Please contact Seeka Team.");
					return session;
				}
				
				long existEmail = getUserEmail(user.getEmail());
				if(existEmail > 0 ){
					session.setStatus("0");
					session.setMessage(" Account activation link is not valid, Please contact Seeka Team.");
					return session;
				}
					
				statement = con.prepareStatement(SQLUpdateQuries.USER_UPDATE_ACTIVE);
				statement.setInt(1, AppConstants.ACTIVE);
				statement.setString(2, user.getUserName());
				statement.setString(3, user.getEmail());
				statement.setString(4, user.getSession().getSessionToken());
				logger.debug(" SQL : "+statement.toString());
				int rowAffected = statement.executeUpdate();
				con.commit();
				if(rowAffected > 0) {
					session.setStatus("1");
					session.setMessage("User successfully activated.");
					//session  = authanticateUser(user.getUserName() , user.getPassword() , con);
				}
		}catch (Exception e) {
			session.setStatus("0");
			session.setMessage("Unable to activate user. Please try again later");
			logger.error("activateUser() : ",e);
		}finally{
			
			try {
				closeStatement(statement);
				closeConnection(con);
			} catch (SQLException e) {
				logger.error("finnaly activateUser() : ",e);
			}
		}
		
		return session;
	}


	@Override
	public User getUser(String email) {
		Connection con = dbUtil.getJNDIConnection();
		PreparedStatement statement = null;
		User user = null;
		try{
			if(con != null){
				statement = con.prepareStatement(SQLSelectQueries.SELECT_USER_BY_EMAIL);
				statement.setString(1, email);
				logger.debug(" SQL : "+statement.toString());
				ResultSet result = statement.executeQuery();
				while(result.next()){
					user = new User();
					user.setEmail(email);
					user.setUserName(result.getString(1));
					Session session = new Session();
					session.setSessionToken(result.getString(2));
					user.setSession(session);
				}				
			}else{
				logger.error("Throw error : getUser , connection is null.");
			}
		}catch(SQLException exe){
				logger.error("getUser()" , exe);
		}finally{
			try {
				closeStatement(statement);
				closeConnection(con);
			} catch (SQLException e) {

				logger.error("finally : getUser : ",e);
			}
		}
		return user;
	}	
	
}
