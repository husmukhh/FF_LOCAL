package com.ff.dao;

public interface SQLSelectQueries {
	String SELECT_COUNTRY = "SELECT country.ID, country.COUNTRY_TXT, country.CNT_DESC, country.COUNTRY_CODE FROM country " ;
	
	String SELECT_USERS_WHERE = "SELECT user.id , sesin.session_token FROM users  user left join user_session sesin on user.id = sesin.user_id "+
			"	WHERE user.username = ? and  user.password= ?";
	String SELECT_USERS_ID_WHERE = "SELECT id FROM users WHERE username = ?";
	String SELECT_USERS_ID_BY_SESSION_WHERE = "SELECT user_id FROM user_session WHERE session_token = ? and session_status = '" + SessionStatus.ACTIVE +"'";

	String SESSION_SELECT = "SELECT session_token from user_session where user_id = ?";
	
	String SELECT_USERS_INFO_WHERE = "SELECT user.* FROM users user WHERE id = ? and session_status = '" + SessionStatus.ACTIVE +"'";	
	String SELECT_USERS_EDU_WHERE = "SELECT userEdu.* FROM user_edu userEdu , user_session userSes WHERE userEdu.user_id =userSes.user_id and  userEdu.user_id = ? and userSes.session_status = '" + SessionStatus.ACTIVE +"'";
	String SELECT_USERS_EDU_ALEVEL_WHERE = "SELECT userEduCamb.* FROM user_edu_a_o userEduCamb WHERE userEduCamb.user_id = ?";
	String SELECT_USERS_EDU_IELTS_TOFFEL_WHERE = "SELECT userEduIelts.* from  user_edu_iel_tof_score where user_id = ?";
	
	String SELECT_COUNTRY_CODE_CACHE = "SELECT COUNTRY_TXT , COUNTRY_CODE FROM list_country";
	
	
}
