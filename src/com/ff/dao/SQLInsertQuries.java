package com.ff.dao;

public interface SQLInsertQuries {

	String SESSION_INSERT = "INSERT INTO    user_session ( session_token, session_status, user_id) VALUES ( ( SELECT  MD5( ROUND( UNIX_TIMESTAMP( CURTIME(4) ) * 1000 ) ) ) ,'ACT' , ?) ";
	String USER_INFO_INSERT = "INSERT INTO user_info ( user_id,first_name,last_name,dob,skype_id,mobile_no,gender,country_origin,citizenship) "
			+ "VALUES (? , ? , ? , ? ,? , ? , ? , ? , ? ) ";
	String USER_INSERT = "INSERT INTO users (username,email,password) values(?,?,?) ";
	String USER_HOBBY_INTEREST = "INSERT INTO user_interest_hobby(user_id,hobby_txt) values(?,?)";
	String USER_CAREER_INTEREST = "INSERT INTO user_interest_carrer(user_id,career_txt , job_role) values(?,?,?)";
	String USER_COUNTRIES_INTREST = "INSERT INTO user_intrest_country(user_id,country_code) values(?,?)";
	
	String USER_EDU_IELTS_TOFFEL_SCROE_INSERT = "INSERT INTO user_edu_iel_tof_score (user_id,`read`,`write`,speak,listen,overall) VALUES(? , ? , ? , ? , ? ,?)";
	String USER_EDU_A_O_SCORE_INSERT = "INSERT INTO user_edu_a_o (user_id,sub_name,grade) VALUES( ? , ? , ?)";
	String USER_EDU_INSERT = "INSERT INTO user_edu(user_id,edu_country,edu_system,edu_sys_score,edu_institue,gpa_score,is_english_medium,toffel_ielts,edu_level) VALUES (? , ? , ? , ? , ? , ? , ?,?,? )";
	
	
}
