package com.ff.dao;

public interface SQLDeleteQuries {

	String USER_INTEREST_CAREER_DELETE 			= "DELETE FROM user_interest_carrer WHERE user_id = ?";
	String USER_INTEREST_HOBBY_DELETE 			= "DELETE FROM user_interest_hobby WHERE user_id = ?";
	String USER_INTEREST_COUNTRY_DELETE 		= "DELETE FROM user_intrest_country WHERE user_id = ?";
	String USER_EDU_DELETE      		= "DELETE FROM user_edu WHERE user_id = ?";
	String USER_EDU_A_O_DELETE  		= "DELETE FROM user_edu_a_o WHERE user_id = ?";
	String USER_EDU_IELTS_TOFFEL_DELETE = "DELETE FROM user_edu_iel_tof_score WHERE user_id = ?";
	
	String USER_SESSION_DELETE = "DELETE FROM user_session WHERE session_token  = ?";
	String USER_INFO_DELETE = "DELETE FROM user_info WHERE user_id = ?";
	
}
