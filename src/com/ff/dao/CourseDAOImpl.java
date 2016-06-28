package com.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.model.AdvanceSearchReq;
import com.ff.model.AppConstants;
import com.ff.model.Course;
import com.ff.model.CourseDetails;
import com.ff.model.Location;
import com.ff.model.School;
import com.ff.model.SponserDetails;
import com.ff.model.Subjects;
import com.ff.model.UserEducation;
import com.ff.util.ApplicationConstant;
import com.ff.util.CountryCodeCache;
import com.ff.util.EducationSystemConstants;
import com.ff.vo.CountryVO;
import com.ff.vo.CourseResultVO;
import com.ff.vo.SearchResultVO;
import com.ff.vo.SearchVO;
import com.ff.vo.UnlockCourseVO;
import com.ff.vo.UnlockedCourseVO;

public class CourseDAOImpl implements CourseDAO{
	
	Logger logger = LoggerFactory.getLogger(CourseDAOImpl.class);
	private  DBUtil dbUtil ;
	
	
	@Override
	public List<School> getAllCountrySchool(int countryId) throws SQLException{
		List<School> schoolList = new ArrayList<School>();
		Statement statement = null;
		Connection con = dbUtil.getJNDIConnection();
		try {
			
			statement = con.createStatement();
			ResultSet result = statement.executeQuery(SQLSelectQueries.SELECT_COUNTRY);
			
			while(result.next()){
				
				logger.debug(result.getString("COUNTRY_TXT"));
			}
			

		} catch (Exception e) {
			logger.error("getAllCountrySchool : ", e);
			throw e;
			
		}finally {
			if(statement != null){
				statement.close();
				closeConnection(con);
			}
		}
		
		return schoolList;
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	/* (non-Javadoc)
	 * @see com.ff.dao.SchoolDAO#search(com.ff.model.SearchVO)
	 */
	@Override
	public SearchResultVO searchCourses(SearchVO searchVO ) {
		
		List<Course> courseList = new ArrayList<Course>();
		SearchResultVO responseData = new SearchResultVO();
		Connection con = dbUtil.getJNDIConnection();
		logger.debug(" in searchCourses(SearchVO searchVO) ");
		  try {
				
					
			
			searchVO.setSearchText(searchVO.getSearchText().replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![")); 
			
			UserEducation userEdu = getUserInfo(searchVO.getSessionId() , con);
			
			if(userEdu != null && searchVO.getPageNo() == 0){
				setResultCount(searchVO);
				search(searchVO, courseList, con, userEdu,responseData);
				}
			else if(userEdu != null && searchVO.getPageNo() > 0 ){
				search(searchVO, courseList, con, userEdu,responseData);
			}else{
				
			}

			
			if(courseList != null && courseList.size() > 0){
				responseData.setStatus("1");
				responseData.setPageNo(searchVO.getPageNo());
				responseData.setData(courseList);
				responseData.setMessage("Results found for your search criteria.");
			}else{
				responseData.setStatus("0");
				responseData.setPageNo(searchVO.getPageNo());
				responseData.setData(null);
				responseData.setMessage("No results found. Please make sure your education information is complete. Try again later.");
			}			
			
			
			return responseData;
		  }catch(Exception exe){
			  exe.printStackTrace();
		  }finally{
			  closeConnection(con);
		  }
			
			return null;
	}

	private void setResultCount(SearchVO searchVO) {
	
		
	}

	private void search(SearchVO searchVO, List<Course> courseList, Connection con, UserEducation userEdu , SearchResultVO searchResultVo)
			throws SQLException {
		{
			StringBuffer searchQuery = new StringBuffer();
			StringBuffer countQuery = new StringBuffer();
			
			logger.debug(" in private method : search(SearchVO searchVO.......) ");	
			if(userEdu.getEduSystem() != null && userEdu.getEduCountry() != null) {
				
				countQuery.append("select count(*) as total_records from university_course_attribute WHERE ");
				
				
				searchQuery.append(" SELECT  Courses, Int_Fees, Currency, Duration , Duration_Time , Cost_Savings, Remarks_, WR_Range, ");
				searchQuery.append(" Cost_Range , Twinning_Program, Recognition , Recognition_Type, Faculty, Course_Type, C_ID, Country, ");
				searchQuery.append(" add_type, add_text, spnsr.sponser_id , spnsr.sponser_name ");
				searchQuery.append(" FROM university_course_attribute ");
				searchQuery.append(" left join course_sponser_details csd  on C_ID = csd.course_id ");
				searchQuery.append(" left join sponser spnsr on spnsr.sponser_id = csd.sponser_id ");
				searchQuery.append("  WHERE ");
				searchQuery.append(" courses like '"+ searchVO.getSearchText()+"%'  AND  " );
				String userEduSystemScore = "0";
				if(userEdu.getEduSystemScore() != null && userEdu.getEduSystemScore() != null)
					userEduSystemScore = userEdu.getEduSystemScore().replace("%", "");
				
				
				updateQueryWithEducation(userEdu, searchQuery, countQuery, userEduSystemScore);

				String country = CountryCodeCache.getCountryName(searchVO.getCountryCode());
				searchQuery.append(" AND country = '").append(country).append("' ");
				countQuery.append(" AND country = '").append(country).append("' ");
				
				searchQuery.append("  order by c_id desc limit ").append( searchVO.getPageNo() * EducationSystemConstants.NO_OF_RECORD_PER_PAGE ).append(",").append(EducationSystemConstants.NO_OF_RECORD_PER_PAGE);
				
				logger.debug("SQL for count : " + countQuery.toString());
				
				logger.debug("SQL for search : " + searchQuery.toString());
				
				
				PreparedStatement statement = con.prepareStatement(searchQuery.toString());
				
				if(searchVO.getPageNo() == 0){
					ResultSet resultCount = statement.executeQuery(countQuery.toString());
					while(resultCount.next()){
						int count = resultCount.getInt("total_records");
						System.out.println("Total Records : " + count);
						searchResultVo.setTotalRecords(count);
					}
					
				}
				
				//statement.setString(1, "'%"+searchVO.getSearchText()+"%'");
				loadResultCourses(courseList, searchQuery, statement);				
			}

			
		}
	}

	private void loadResultCourses(List<Course> courseList, StringBuffer searchQuery, PreparedStatement statement)
			throws SQLException {
		ResultSet result = statement.executeQuery(searchQuery.toString());
		
		while(result.next()){
			Course course = new Course ();

			course.setCountry(result.getString("Country"));
			course.setCourseId(result.getInt("C_ID"));
			course.setCourseTitle(result.getString("Courses"));
			course.setCostRange(result.getString("Cost_Range"));
			course.setDurationTime(result.getString("Duration_Time"));
			course.setDurationType(result.getString("Duration"));
			course.setRecognition(result.getString("Recognition"));
			course.setRecognitionType(result.getString("Recognition_Type"));
			course.setRemarks(result.getString("Remarks_"));
			course.setAvgWorldRank(result.getString("WR_Range"));
			
			if(result.getInt("sponser_id") > 0){
				SponserDetails spnDet = new SponserDetails();
				spnDet.setSponserId(result.getInt("sponser_id"));
				spnDet.setSponserName(result.getString("sponser_name"));
				spnDet.setAddText(result.getString("add_text"));
				spnDet.setCourseId(result.getInt("C_ID"));
				spnDet.setAddType(result.getInt("add_type"));
				spnDet.setSponserLogoUrl(ApplicationConstant.LOGO_URL_PATH + ApplicationConstant.LOGO_PREFIX + spnDet.getSponserId() + ApplicationConstant.LOGO_FORMAT);
				course.setSponserDetails(spnDet);
			}
			
			courseList.add(course);
		}
	}

	private void updateQueryWithEducation(UserEducation userEdu, StringBuffer searchQuery, StringBuffer countQuery,
			String userEduSystemScore) {
		switch (userEdu.getEduSystem()){
		
		case EducationSystemConstants.BAN_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.BAN_HSC);
			
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.BAN_HSC ).append(",'%','') <= ").append(userEduSystemScore);
			countQuery.append(" REPLACE (").append(EducationSystemConstants.BAN_HSC ).append(",'%','') <= ").append(userEduSystemScore);
			
			searchQuery.append(" AND  REPLACE (").append(EducationSystemConstants.BAN_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.BAN_HSC ).append(",'%','') > ").append(0);					
			break;
		}
		case EducationSystemConstants.CHINA_GAO_ER : {
			logger.debug(" search() Education System :  " + EducationSystemConstants.CHINA_GAO_ER);
			searchQuery.append(" ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" <= ").append(userEdu.getEduSystemScore());


			searchQuery.append(" AND ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" > ").append(0);
			break;
			}
		case EducationSystemConstants.CHINA_GAOKAO_Gao_San: {
			logger.debug(" search() Education System :  " + EducationSystemConstants.CHINA_GAOKAO_Gao_San);
			searchQuery.append(" ").append(EducationSystemConstants.CHINA_GAOKAO_Gao_San ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" <= ").append(userEdu.getEduSystemScore());
			
			
			searchQuery.append(" AND ").append(EducationSystemConstants.CHINA_GAOKAO_Gao_San ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.CHINA_GAO_ER ).append(" > ").append(0);
			
			break;
			}
		case EducationSystemConstants.GLOB_A_LEV : {
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_A_LEV);
			 List<String> subjGradeList = new ArrayList( userEdu.getCambrigeSubGrds().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(int i = 0 ; i < subjGradeList.size()-2 ; i++){
				 String grade = subjGradeList.get(i);
				 grade = grade.replace("+", "");
				 grade = grade.replace("-", "");
				 
				 if( i  == subjGradeList.size()-3	){
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.GLOB_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
				 	 countQuery.append(" UPPER(").append(EducationSystemConstants.GLOB_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");}
				 else{
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.GLOB_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
				 	 countQuery.append(" UPPER(").append(EducationSystemConstants.GLOB_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
				 	}

			 }
			break;
			}
		case EducationSystemConstants.GLOB_DEGREE : {
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_DEGREE);
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_DEGREE ).append(" <=").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_DEGREE ).append(" <=").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_DEGREE ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_DEGREE ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_DIPLOMA : {
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_DIPLOMA);
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_DIPLOMA ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_DIPLOMA ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_DIPLOMA ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_DIPLOMA ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_FIRST_YEAR : {
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_FIRST_YEAR);
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_FIRST_YEAR ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_FIRST_YEAR ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_FIRST_YEAR ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_FIRST_YEAR ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_GPA :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_GPA);					
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_GPA ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_GPA ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_GPA ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_GPA ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_IB :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_IB);					
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_IB ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_IB ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_IB ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_IB ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_IF :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_IF);					
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_IF ).append(" <=").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_IF ).append(" <=").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_IF ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_IF ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.GLOB_SECOND_YEAR :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOB_SECOND_YEAR);
			searchQuery.append(" ").append(EducationSystemConstants.GLOB_SECOND_YEAR ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOB_SECOND_YEAR ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.GLOB_SECOND_YEAR ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.GLOB_SECOND_YEAR ).append(" > ").append(0);
			
			break;
			}
		case EducationSystemConstants.GLOBE_O_LEV :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.GLOBE_O_LEV);
			searchQuery.append(" ").append(EducationSystemConstants.GLOBE_O_LEV ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.GLOBE_O_LEV ).append(" <= ").append(userEdu.getEduSystemScore());
			break;
			}
		case EducationSystemConstants.IND_HSC_ISC_SSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.IND_HSC_ISC_SSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.IND_HSC_ISC_SSC ).append(",'%','') <= ").append(userEduSystemScore);
			countQuery.append(" REPLACE (").append(EducationSystemConstants.IND_HSC_ISC_SSC ).append(",'%','') <= ").append(userEduSystemScore);
			
			searchQuery.append(" AND  REPLACE (").append(EducationSystemConstants.IND_HSC_ISC_SSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.IND_HSC_ISC_SSC ).append(",'%','') > ").append(0);
			
			break;
			}
		case EducationSystemConstants.INDO_SMU :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.INDO_SMU);
			searchQuery.append(" ").append(EducationSystemConstants.INDO_SMU ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.INDO_SMU ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.INDO_SMU ).append(" > ").append( 0 );
			countQuery.append(" AND ").append(EducationSystemConstants.INDO_SMU ).append(" > ").append( 0 );					
			break;
			}
		case EducationSystemConstants.IRAN_DM_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.IRAN_DM_HSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.IRAN_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" REPLACE (").append(EducationSystemConstants.IRAN_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND REPLACE (").append(EducationSystemConstants.IRAN_DM_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.IRAN_DM_HSC ).append(",'%','') > ").append(0);
			
			break;
			}
		case EducationSystemConstants.JAPAN_KSS :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.JAPAN_KSS);
			searchQuery.append(" ").append(EducationSystemConstants.JAPAN_KSS ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.JAPAN_KSS ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.JAPAN_KSS ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.JAPAN_KSS ).append(" > ").append(0);
			
			break;
			}
		case EducationSystemConstants.KSA_DM_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.KSA_DM_HSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.KSA_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" REPLACE (").append(EducationSystemConstants.KSA_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND REPLACE (").append(EducationSystemConstants.KSA_DM_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.KSA_DM_HSC ).append(",'%','') > ").append(0);					
			break;
			}
		case EducationSystemConstants.MAL_SPM :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.MAL_SPM);
			searchQuery.append(" ").append(EducationSystemConstants.MAL_SPM ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.MAL_SPM ).append(" <= ").append(userEdu.getEduSystemScore());
			break;
			}
		case EducationSystemConstants.MAL_STPM :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.MAL_STPM);
			List<String> subjGradeList = new ArrayList( userEdu.getCambrigeSubGrds().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(int i = 0 ; i < subjGradeList.size()-2 ; i++){
				 String grade = subjGradeList.get(i);
				 grade = grade.replace("+", "");
				 grade = grade.replace("-", "");
				 
				 if( i  == subjGradeList.size()-3	){
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.MAL_STPM_).append(i+1).append( ") <= '").append(grade.toUpperCase()).append("'  ");
				 	 countQuery.append(" UPPER(").append(EducationSystemConstants.MAL_STPM_).append(i+1).append( ") <= '").append(grade.toUpperCase()).append("'  ");}
				 else{
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.MAL_STPM_).append(i+1).append( ") <= '").append(grade.toUpperCase()).append("' AND ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.MAL_STPM_).append(i+1).append( ") <= '").append(grade.toUpperCase()).append("' AND ");
					 }

			 }
			break;
			}
		case EducationSystemConstants.NEP_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.NEP_HSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.NEP_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" REPLACE (").append(EducationSystemConstants.NEP_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND REPLACE (").append(EducationSystemConstants.NEP_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.NEP_HSC ).append(",'%','') > ").append(0);					
			break;
			}
		case EducationSystemConstants.PAK_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.PAK_HSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.PAK_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" REPLACE (").append(EducationSystemConstants.PAK_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND  REPLACE (").append(EducationSystemConstants.PAK_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.PAK_HSC ).append(",'%','') > ").append(0);
			
			break;
			}
		case EducationSystemConstants.PHILIP_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.PHILIP_HSC);
			searchQuery.append(" REPLACE (").append(EducationSystemConstants.PHILIP_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" REPLACE (").append(EducationSystemConstants.PHILIP_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND REPLACE (").append(EducationSystemConstants.PHILIP_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND REPLACE (").append(EducationSystemConstants.PHILIP_HSC ).append(",'%','') > ").append(0);
			
			break;
			}
		case EducationSystemConstants.SAT_USA :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.SAT_USA);
			searchQuery.append(" ").append(EducationSystemConstants.SAT_USA ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.SAT_USA ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.SAT_USA ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.SAT_USA ).append(" > ").append(0);					
			break;
			}
		case EducationSystemConstants.SGP_A_LEVE :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.SGP_A_LEVE);
			List<String> subjGradeList = new ArrayList( userEdu.getCambrigeSubGrds().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(int i = 0 ; i < subjGradeList.size()-2 ; i++){
				 String grade = subjGradeList.get(i);
				 grade = grade.replace("+", "");
				 grade = grade.replace("-", "");
				 
				 if( i  == subjGradeList.size()-3	){
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.SGP_A_LEVE_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.SGP_A_LEVE_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 }
				 else{
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.SGP_A_LEVE_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.SGP_A_LEVE_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 }

			 }
			break;
			}
		case EducationSystemConstants.SGP_O_LEVE :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.SGP_O_LEVE);
			searchQuery.append(" ").append(EducationSystemConstants.SGP_O_LEVE ).append(" >= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.SGP_O_LEVE ).append(" >= ").append(userEdu.getEduSystemScore());
			break;
			}
		case EducationSystemConstants.SRI_A_LEV :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.SRI_A_LEV);
			List<String> subjGradeList = new ArrayList( userEdu.getCambrigeSubGrds().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(int i = 0 ; i < subjGradeList.size()-2 ; i++){
				 String grade = subjGradeList.get(i);
				 grade = grade.replace("+", "");
				 grade = grade.replace("-", "");
				 if( i  == subjGradeList.size()-3	){
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.SRI_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.SRI_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 }
				 else{
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.SRI_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.SRI_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 }
			 }
			break;
			}
		case EducationSystemConstants.SRI_O_LEV :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.SRI_O_LEV);
			searchQuery.append(" ").append(EducationSystemConstants.SRI_O_LEV ).append(" >= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.SRI_O_LEV ).append(" >= ").append(userEdu.getEduSystemScore());
			break;
			}
		case EducationSystemConstants.THA_MathayomSuksa_5_6 :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.THA_MathayomSuksa_5_6);
			searchQuery.append(" ").append(EducationSystemConstants.THA_MathayomSuksa_5_6 ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.THA_MathayomSuksa_5_6 ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.THA_MathayomSuksa_5_6 ).append(" > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.THA_MathayomSuksa_5_6 ).append(" > ").append(0);
			
			break;
			}
		case EducationSystemConstants.UAE_REG_DM_HSC :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.UAE_REG_DM_HSC);
			searchQuery.append(" ").append(EducationSystemConstants.UAE_REG_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.UAE_REG_DM_HSC ).append(",'%','') <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" AND ").append(EducationSystemConstants.UAE_REG_DM_HSC ).append(",'%','') > ").append(0);
			countQuery.append(" AND ").append(EducationSystemConstants.UAE_REG_DM_HSC ).append(",'%','') > ").append(0);
			
			break;
			}
		case EducationSystemConstants.UK_A_LEV :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.UK_A_LEV);
			List<String> subjGradeList = new ArrayList( userEdu.getCambrigeSubGrds().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(int i = 0 ; i < subjGradeList.size()-2 ; i++){
				 String grade = subjGradeList.get(i);
				 
				 grade = grade.replace("+", "");
				 grade = grade.replace("-", "");
				 if( i  == subjGradeList.size()-3	){
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.UK_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.UK_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("'  ");
					 }
				 else{
					 searchQuery.append(" UPPER(").append(EducationSystemConstants.UK_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 countQuery.append(" UPPER(").append(EducationSystemConstants.UK_A_LEV_).append(i+1).append( ") >= '").append(grade.toUpperCase()).append("' AND ");
					 }

			 }
			break;
			}
		case EducationSystemConstants.UK_O_LEV :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.UK_O_LEV);
			searchQuery.append(" ").append(EducationSystemConstants.UK_O_LEV ).append(" >= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.UK_O_LEV ).append(" >= ").append(userEdu.getEduSystemScore());
			break;
			}
		case EducationSystemConstants.US_GPA :{
			logger.debug(" search() Education System :  " + EducationSystemConstants.US_GPA);
			searchQuery.append(" ").append(EducationSystemConstants.US_GPA ).append(" <= ").append(userEdu.getEduSystemScore());
			countQuery.append(" ").append(EducationSystemConstants.US_GPA ).append(" <= ").append(userEdu.getEduSystemScore());
			
			searchQuery.append(" ").append(EducationSystemConstants.US_GPA ).append(" > ").append(0);
			countQuery.append(" ").append(EducationSystemConstants.US_GPA ).append(" > ").append(0);					
			break;
			}

		}
		
		
		if("Y".equals( userEdu.getIsEngMedium() )){
			
		}else
		if("IELTS".equalsIgnoreCase( userEdu.getIeltsToffel() )){
			List<String> subjGradeList = new ArrayList( userEdu.getIeltsToffelScore().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(String subjName : userEdu.getCambrigeSubGrds().getSubjects().keySet()){
				 String score = (String) userEdu.getCambrigeSubGrds().getSubjects().get(subjName);
				 if(EducationSystemConstants.READ.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND  ").append(EducationSystemConstants.IELTS_Reading).append( ">=").append(score);
					 countQuery.append(" AND  ").append(EducationSystemConstants.IELTS_Reading).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.WRITE.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Writing).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Writing).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.SPEAK.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Speaking).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Speaking).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.LISTEN.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Listening).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Listening).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.OVERALL.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Overall_Score).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Overall_Score).append( ">=").append(score);
				 }					 
				 
			 }				
		}else
		if("TOFFEL".equalsIgnoreCase( userEdu.getIeltsToffel() )){
			List<String> subjGradeList = new ArrayList( userEdu.getIeltsToffelScore().getSubjects().values()) ;
			 Collections.sort(subjGradeList);
			 for(String subjName : userEdu.getCambrigeSubGrds().getSubjects().keySet()){
				 String score = (String) userEdu.getCambrigeSubGrds().getSubjects().get(subjName);
				 if(EducationSystemConstants.READ.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Reading).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Reading).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.WRITE.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Writing).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Writing).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.SPEAK.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Speaking).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Speaking).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.LISTEN.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Listening).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Listening).append( ">=").append(score);
				 }else
				 if(EducationSystemConstants.OVERALL.equalsIgnoreCase(subjName)){
					 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Overall_Score).append( ">=").append(score);
					 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Overall_Score).append( ">=").append(score);
				 }					 
				 
			 }				
		}
		else{
			 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Reading).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Writing).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Speaking).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Listening).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Overall_Score).append( "=").append(0);
			 
			 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Reading).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Writing).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Speaking).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Listening).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.TOFEL_Overall_Score).append( "=").append(0);
			 
			 
			 searchQuery.append(" AND  ").append(EducationSystemConstants.IELTS_Reading).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Writing).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Speaking).append( "=").append(0);
			 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Listening).append( "=").append(0);	
			 searchQuery.append(" AND ").append(EducationSystemConstants.IELTS_Overall_Score).append( "=").append(0);
			 
			 countQuery.append(" AND  ").append(EducationSystemConstants.IELTS_Reading).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Writing).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Speaking).append( "=").append(0);
			 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Listening).append( "=").append(0);	
			 countQuery.append(" AND ").append(EducationSystemConstants.IELTS_Overall_Score).append( "=").append(0);				 
			 
		};
	}


	
	
	
	

	private UserEducation getUserInfo(String sessionId , Connection con) {
		long userId  = getUserIdBySession(sessionId , con);
		
		UserEducation userEdu = getUserEducation(userId,con);
		return userEdu;
	}

	private UserEducation getUserEducation(long userId, Connection con) {
		PreparedStatement statement = null;
		PreparedStatement userEduStatement = null;
		PreparedStatement userEduIeltsStatement = null;
		UserEducation userEdu = new UserEducation();
		try{
			//" SELECT userEdu.* FROM user_edu userEdu , user_session userSes WHERE 
			// userEdu.user_id =userSes.user_id and  userEdu.user_id = ? and 
			// userSes.session_status = '" + SessionStatus.ACTIVE +"'";
			statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_EDU_WHERE);
			statement.setLong(1, userId);
			ResultSet result = statement.executeQuery();
			while(result.next()){
				userEdu.setEduCountry(result.getString("edu_country"));
				userEdu.setEduSystem(result.getString("edu_system"));
				userEdu.setEduSystemScore(result.getString("edu_sys_score"));
				userEdu.setIsEngMedium(result.getString("is_english_medium"));
				userEdu.setGpaScore(result.getString("gpa_score"));
				userEdu.setIeltsToffel(result.getString("toffel_ielts"));
				
			}
			
			if(EducationSystemConstants.GLOB_A_LEV.equals(userEdu.getEduSystem() ) ||
					EducationSystemConstants.UK_A_LEV.equals(userEdu.getEduSystem() ) ||
					EducationSystemConstants.SGP_A_LEVE.equals(userEdu.getEduSystem() ) ||
					EducationSystemConstants.MAL_STPM.equals(userEdu.getEduSystem() ) ||
					EducationSystemConstants.SRI_A_LEV.equals(userEdu.getEduSystem() ) 
					) {
				userEduStatement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_EDU_ALEVEL_WHERE);
				userEduStatement.setLong(1, userId);
				ResultSet resultEduAlevl = userEduStatement.executeQuery();
				Subjects alevelSubj = new Subjects();
				while(resultEduAlevl.next()){
					alevelSubj.getSubjects().put(resultEduAlevl.getString("sub_name"), resultEduAlevl.getString("grade"));
				}

				userEdu.setCambrigeSubGrds(alevelSubj);
			}
			
/*			if(EducationSystemConstants.HSC.equals(userEdu.getEduSystem() ) ) {
				
				userEdu.setEduSystem(DBUtil.HSC_EDU_MAP.get(userEdu.getEduCountry()));
			}	*/		
			
			if(AppConstants.IS_IELTS_TOFFEL.equals(userEdu.getIeltsToffel() )) {
				userEduIeltsStatement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_EDU_IELTS_TOFFEL_WHERE);
				userEduIeltsStatement.setLong(1, userId);
				ResultSet resultIelts = userEduIeltsStatement.executeQuery();

				Subjects ieltsSubj = new Subjects();
				String read = null;
				String write = null;
				String speak = null;
				String listen = null;
				
				while(resultIelts.next()){
					read = resultIelts.getString("read");
					write = resultIelts.getString("write");
					speak = resultIelts.getString("speak");
					listen = resultIelts.getString("listen");
				}
				
				ieltsSubj.getSubjects().put(AppConstants.READ, read);
				ieltsSubj.getSubjects().put(AppConstants.WRITE, write);
				ieltsSubj.getSubjects().put(AppConstants.SPEAK, speak);
				ieltsSubj.getSubjects().put(AppConstants.LISTEN, listen);
				userEdu.setIeltsToffelScore(ieltsSubj);
			}
			
			
		}catch(SQLException exe){
			exe.printStackTrace();
		}finally{
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return userEdu;
	}

	private long getUserIdBySession(String sessionId, Connection con) {
		PreparedStatement statement = null;
		long userId = -1L;
		try{
			
			statement = con.prepareStatement(SQLSelectQueries.SELECT_USERS_ID_BY_SESSION_WHERE);
			statement.setString(1, sessionId);
			ResultSet result = statement.executeQuery();
			while(result.next()){
				userId = result.getLong(1);
			}
			
		}catch(SQLException exe){
			
			exe.printStackTrace();
		}finally{
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return userId;		
		
	}

	
	public  SearchResultVO search(SearchVO searchVO , Connection con) {
		List<Course> courseList = new ArrayList<Course>();
		try{
			searchVO.setSearchText(searchVO.getSearchText().replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![")); 
			
			UserEducation userEdu = getUserInfo(searchVO.getSessionId() , con);
			
			if(userEdu != null && searchVO.getPageNo() == 1){
				setResultCount(searchVO);
				//search(searchVO, courseList, con, userEdu);
				}
			else if(userEdu != null && searchVO.getPageNo() > 1){
				//search(searchVO, courseList, con, userEdu);
			}else{
				
			}

			SearchResultVO responseData = new SearchResultVO();
			if(courseList != null && courseList.size() > 0){
				responseData.setStatus("1");
				responseData.setPageNo(searchVO.getPageNo());
				responseData.setData(courseList);
				responseData.setMessage("Results found for your search criteria.");
			}else{
				responseData.setStatus("0");
				responseData.setPageNo(searchVO.getPageNo());
				responseData.setData(null);
				responseData.setMessage("No results found. Please make sure your education information is complete. Try again later.");
			}			
			
			
			return responseData;
		  }catch(Exception exe){
			  exe.printStackTrace();
		  }finally{
			  
		  }
			
			return null;

	}	
	

	

 @Override
	public CourseResultVO unlockCourse(UnlockCourseVO unlockCourseVO) throws SQLException {
	 /**
	  * Check if user already unlocked course than just return to course details else deduct points and unlock course*/
		String checkUnlockQuery = "SELECT count(*) as total from user_unlocked_courses unlock_course "
				+ " inner join  users reg_user on  unlock_course.user_id = reg_user.id  "
				+ " where unlock_course.user_id = ? and  unlock_course.course_id = ? " ;
		int count = -1;
		PreparedStatement statement = null;
		Connection con = dbUtil.getJNDIConnection();
		try{
			
			con.setAutoCommit(false);
			statement = con.prepareStatement(checkUnlockQuery);
			long userId = getUserIdBySession(unlockCourseVO.getSessionId(), con);
			statement.setLong(1, userId);
			statement.setInt(2, unlockCourseVO.getCourseId());
			ResultSet result = statement.executeQuery();
			while(result.next()){	count = result.getInt("total");	}
			
			if(count > 0 ){
				return getCourseDetails(unlockCourseVO.getCourseId() , con);
			}else if(hasCredits(unlockCourseVO.getSessionId() ,unlockCourseVO.getCoursePoints(), con)  ){
				updateUserCredits(userId , unlockCourseVO.getCourseId(), unlockCourseVO.getCoursePoints(), con);
				updateUserUnlockCourse(userId ,unlockCourseVO.getCourseId() ,con);
				con.commit();
				return getCourseDetails(unlockCourseVO.getCourseId() , con);
			}else{
				
			}
			
		}catch(SQLException exe){
			throw exe;
		}finally{
			if(statement != null)
			statement.close();
			closeConnection(con);
		}
		return null;
	}

	private void updateUserUnlockCourse(long userId ,int courseId , Connection con)throws SQLException {
		
		String insertUserUnlockCourseQuery = "insert into user_unlocked_courses (user_id, course_id, unlock_timestamp) values(?, ?, ?)";
		PreparedStatement insUsrUnlcCrsStmt = null;
		try{
			insUsrUnlcCrsStmt = con.prepareStatement(insertUserUnlockCourseQuery);
			insUsrUnlcCrsStmt.setLong(1, userId);
			insUsrUnlcCrsStmt.setInt(2, courseId);
			insUsrUnlcCrsStmt.setTimestamp(3, new Timestamp( new Date().getTime()) );
			int rowAffected = insUsrUnlcCrsStmt.executeUpdate();
			
			
		}catch(SQLException exe){
			throw exe;
		}finally{
			if(insUsrUnlcCrsStmt != null){
				insUsrUnlcCrsStmt.close();
			}
		}
	}

	private void updateUserCredits(long userId , int courseId , int coursePoints , Connection con) throws SQLException{
			String updateUserCreditQuery = " update user_credits set balance_points =  balance_points - ? , t_date = ? where user_id = ?";
			String insertUserCreditHistoryQuery = " insert into  user_transaction_history (u_id, t_timestamp, course_id, points) values(?,?,?,?)";
			
			PreparedStatement updateCreditStm = null;
			PreparedStatement insertCrdtHistoryStm = null;
			try{ 
				updateCreditStm = con.prepareStatement(updateUserCreditQuery);
				updateCreditStm.setInt(1, coursePoints);
				Timestamp transTime = new Timestamp( new Date().getTime());
				updateCreditStm.setTimestamp(2, transTime);
				updateCreditStm.setLong(3, userId);
				int crdtRow = updateCreditStm.executeUpdate();
				
				if(crdtRow > 0) {
				insertCrdtHistoryStm = con.prepareStatement(insertUserCreditHistoryQuery);
				insertCrdtHistoryStm.setLong(1, userId);
				insertCrdtHistoryStm.setTimestamp(2,transTime);
				insertCrdtHistoryStm.setInt(3,courseId);
				insertCrdtHistoryStm.setInt(4,coursePoints);
				crdtRow = insertCrdtHistoryStm.executeUpdate();
				}
				
			}catch(SQLException exe){
				throw exe;
			}finally{
				if(updateCreditStm != null)updateCreditStm.close();
				if(insertCrdtHistoryStm != null) insertCrdtHistoryStm.close();
			}
	
	}

	private boolean hasCredits(String sessionId , int coursePoints , Connection con)  throws SQLException{
		String checkUserCredits = " select  balance_points from user_credits usr_crd	" +  
				"	inner join users usr on usr.id = usr_crd.user_id	" +
				"	inner join user_session usr_sion on usr.id =  usr_sion.user_id	" +
				"	where usr_sion.session_token = ? ";
		PreparedStatement statement = null;
		try {
			
			statement = con.prepareStatement(checkUserCredits);
			statement.setString(1, sessionId);
			ResultSet result = statement.executeQuery();
			int balancePoints = -1;
			while(result.next()) { balancePoints = result.getInt("balance_points");}
	
			if(balancePoints >= coursePoints){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}finally{
			if(statement != null){
				statement.close();
			}
		}
		return false;
	}

	private CourseResultVO getCourseDetails(int courseId , Connection con) throws SQLException{
	
		CourseResultVO  courseResult = new CourseResultVO();
		String courseDetailsQuery = "select  "+
				"course.University,"+
				"course.City,"+
				"course.C_ID,"+
				"course.Courses,"+
				"course.Int_Fees,"+
				"course.Currency,"+
				"course.Currency_Time,"+
				"course.Duration,"+
				"course.Duration_Time,"+
				"course.Cost_Savings,"+
				"course.Cost_Range,"+
				"course.Remarks_,"+
				"course.Twinning_Program,"+
				"course.Country,"+
				"course.World_Ranking,"+
				"course.WR_Range,"+
				"course.Recognition,"+
				"course.Recognition_Type,"+
				"course.Website,"+
				"course.Local_Fees,"+
				"course.Faculty,"+
				"course.Course_Type, " +
	
				"school.Accredited,"+
				"school.Int_Ph_num," +
				"school.Int_Emails," +
				"school.Website," +
				"school.T_num_of_stu,"+
				"school.Longitude," +
				"school.Address," +
				"school.Schlr_finan_asst," +
				"school.YOUTUBE_Link," +
				"school.Corse_Start_Date," +
				"school.Visa_Work_Benefits," +
				"school.Emp_career_dev," +
				"school.Couns_personal_acad,"+
				"school.Study_Library_Support,"+
				"school.About_Us_Info," +
				"school.Opening_hour"+
				 
				 " from university_course_attribute course "
				 + " left join school_detail  school on  Univeristy_Name = University "
				 + " where c_Id = ?";
		
		
		PreparedStatement statement = null;
		try {
			
			statement = con.prepareStatement(courseDetailsQuery);
			statement.setLong(1, courseId);
			ResultSet result = statement.executeQuery();
			CourseDetails courseDetail = null;
			while(result.next()) { 
				courseDetail = new CourseDetails();
				courseDetail.setAvgWorldRank(result.getString("WR_Range"));
				courseDetail.setCity(result.getString("City"));
				courseDetail.setCostRange(result.getString("Cost_Range"));
				courseDetail.setCostSavings(result.getString("Cost_Savings"));
				courseDetail.setCountry(result.getString("Country"));
				courseDetail.setCourseId(result.getInt("C_ID"));
				courseDetail.setCourseTitle(result.getString("Courses"));
				courseDetail.setCourseType(result.getString("Course_Type"));
				courseDetail.setCurrency(result.getString("Currency"));
				courseDetail.setCurrencyTime(result.getString("Currency_Time"));
				courseDetail.setDurationTime(result.getString("Duration_Time"));
				courseDetail.setDurationType(result.getString("Duration"));
				courseDetail.setFaculty(result.getString("Faculty"));
				courseDetail.setIntFees(result.getString("Int_Fees"));
				courseDetail.setRecognition(result.getString("Recognition"));
				courseDetail.setRecognitionType(result.getString("Recognition_Type"));
				courseDetail.setRemarks(result.getString("Remarks_"));
				courseDetail.setTwinningProgram(result.getString("Twinning_Program"));
				courseDetail.setUniversity(result.getString("University"));
				courseDetail.setWebsite(result.getString("Website"));
				courseDetail.setWorldRanking(result.getString("World_Ranking"));
				
				courseDetail.getUniversityInfo().setAccredited(result.getString("Accredited"));
				courseDetail.getUniversityInfo().setPhone(result.getString("Int_Ph_num"));
				courseDetail.getUniversityInfo().setEmail(result.getString("Int_Emails"));
				courseDetail.getUniversityInfo().setTotalNoOfInst(result.getString("T_num_of_stu"));
				courseDetail.getUniversityInfo().setWebsite(result.getString("Website"));
				courseDetail.getUniversityInfo().setLongitude(result.getString("Longitude"));
				courseDetail.getUniversityInfo().setAddress(result.getString("Address"));
				courseDetail.getUniversityInfo().setScholarshipAvail(result.getString("Schlr_finan_asst"));
				courseDetail.getUniversityInfo().setYoutubeLink(result.getString("YOUTUBE_Link"));
				courseDetail.getUniversityInfo().setCourseStart(result.getString("Corse_Start_Date"));
				courseDetail.getUniversityInfo().setVisaWorkBenifits(result.getString("Visa_Work_Benefits"));
				courseDetail.getUniversityInfo().setAboutUs(result.getString("About_Us_Info"));
				courseDetail.getUniversityInfo().setOpeningTime(result.getString("Opening_hour"));
				
				courseDetail.getUniversityInfo().getServices().setEmpCareerDev(result.getString("Emp_career_dev"));
				courseDetail.getUniversityInfo().getServices().setCounsPersonalAcad(result.getString("Couns_personal_acad"));
				courseDetail.getUniversityInfo().getServices().setStudyLibrarySupport(result.getString("Study_Library_Support"));
	
				
				}
			if(courseDetail != null){
				courseResult.setMessage("Course unlocked successfully.");
				courseResult.setStatus("1");
			}else{
				courseResult.setMessage("Due to some technical problems unable to unlock course. Please try later.");
				courseResult.setStatus("0");			
			}
			courseResult.setData(courseDetail);
	
		} catch (SQLException e) {
			courseResult.setMessage("Due to some technical problems unable to unlock course. Please try later.");
			courseResult.setStatus("0");
			e.printStackTrace();
			
		}finally{
			if(statement != null){
				statement.close();
				closeConnection(con);
			}
		}	
		return courseResult;
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

	public static void main(String abc[]){
		  DBUtil dbUtil = new DBUtil(); 
		 Connection con2 = dbUtil.getStandAloneConnection();	
		 if(con2 != null){
			 SearchVO searchVO = new SearchVO();
			 searchVO.setCountryCode("SZ");
			 searchVO.setSearchText("Business");
			 searchVO.setSessionId("d2a757a6aa875a5a46491bce5a522895");
			 searchVO.setPageNo(1);
			 
			 CourseDAOImpl searchDAO = new CourseDAOImpl();
			 SearchResultVO searchResultVO = searchDAO.search(searchVO, con2);
			 
			 System.out.println(searchResultVO != null);
		 }
		 
	
		 
	 }
	
	@Override
	public UnlockedCourseVO getUserUnlockedCourses(String sessionToken) {
		UnlockedCourseVO ulocCrsVo = new UnlockedCourseVO();
		List<Course> courseList = new ArrayList<Course>();
		StringBuffer searchQuery = new StringBuffer();
		searchQuery.append(" SELECT  Courses, Int_Fees, Currency, Duration , Duration_Time , Cost_Savings, Remarks_, WR_Range, ");
		searchQuery.append(" Cost_Range , Twinning_Program, Recognition , Recognition_Type, Faculty, Course_Type, C_ID, Country ");
		searchQuery.append(" FROM university_course_attribute ");
		searchQuery.append(" inner join  user_unlocked_courses uuc  on C_ID = uuc.course_id ");
		searchQuery.append(" inner join  users usr  on uuc.user_id = usr.id ");
		searchQuery.append(" inner join  user_session usr_sesion  on usr.id = usr_sesion.user_id ");
		searchQuery.append("  WHERE usr_sesion.session_token = ?");
		
		PreparedStatement statement = null;
		Connection con = dbUtil.getJNDIConnection();
		try {
					
			statement = con.prepareStatement(searchQuery.toString());
			statement.setString(1, sessionToken);
			logger.debug("SQL :" + statement.toString());
			ResultSet result = statement.executeQuery();
			while(result.next()){
				Course course = new Course ();
	
				course.setCountry(result.getString("Country"));
				course.setCourseId(result.getInt("C_ID"));
				course.setCourseTitle(result.getString("Courses"));
				course.setCostRange(result.getString("Cost_Range"));
				course.setDurationTime(result.getString("Duration_Time"));
				course.setDurationType(result.getString("Duration"));
				course.setRecognition(result.getString("Recognition"));
				course.setRecognitionType(result.getString("Recognition_Type"));
				course.setRemarks(result.getString("Remarks_"));
				course.setAvgWorldRank(result.getString("WR_Range"));
				courseList.add(course);
			}		
			
			ulocCrsVo.setData(courseList);
			ulocCrsVo.setMessage("User unlocked courses successfully loaded");
			ulocCrsVo.setStatus("1");
			ulocCrsVo.setTotalRecords(courseList.size());
			
			}catch (SQLException e) {
				ulocCrsVo.setMessage("Due to some technical problems unable to unlock course. Please try later.");
				ulocCrsVo.setStatus("0");
				logger.error(" getUserUnlockedCourses() : ",e); 
				
			}finally{
				closeStatment(statement);
				closeConnection(con);;
			}		
		return ulocCrsVo;
	}

	private void closeStatment(PreparedStatement statement) {
		if(statement != null){
			try{
				statement.close();
			}catch(Exception e){
				logger.error("closeStatement() : ",e);
			}
		}
	}

@Override
public SearchResultVO advanceSearchCourses(AdvanceSearchReq advanceSearchVO) {
	StringBuffer searchQuery = new StringBuffer(); 
	StringBuffer countQuery = new StringBuffer();
	
	SearchResultVO responseData = new SearchResultVO();
	
	List<Course> courseList = new ArrayList<Course>();
	Connection con = dbUtil.getJNDIConnection();	
	
	countQuery.append("select count(*) as total_records from university_course_attribute WHERE ");
	
	searchQuery.append(" SELECT  Courses, Int_Fees, Currency, Duration , Duration_Time , Cost_Savings, Remarks_, WR_Range, ");
	searchQuery.append(" Cost_Range , Twinning_Program, Recognition , Recognition_Type, Faculty, Course_Type, C_ID, Country, City, ");
	searchQuery.append(" add_type, add_text, spnsr.sponser_id , spnsr.sponser_name ");
	searchQuery.append(" FROM university_course_attribute ");
	searchQuery.append(" left join course_sponser_details csd  on C_ID = csd.course_id ");
	searchQuery.append(" left join sponser spnsr on spnsr.sponser_id = csd.sponser_id ");
	searchQuery.append("  WHERE ");
	

	if (advanceSearchVO.getCourses() != null && advanceSearchVO.getCourses().length > 0) {
		searchQuery.append(" ( " ); 		
		 for (int i = 0 ; i < advanceSearchVO.getCourses().length ; i++ ){
			 if (i >= 1){ 
				 searchQuery.append(" courses like '"+ advanceSearchVO.getCourses()[i]+"%'  " );
				 break;
			 }else{
				 searchQuery.append(" courses like '"+ advanceSearchVO.getCourses()[i]+"%' OR " );
			 }
			 
		 }
	 
      searchQuery.append(" ) " );	
	}
	
     if(advanceSearchVO.getCostEnd() > 0){
    	 searchQuery.append(" AND ( Cost_Range between ").append(advanceSearchVO.getCostStart()).append( " AND  ").append(advanceSearchVO.getCostEnd()).append(") ");
    	 
     }
  
     if ( advanceSearchVO.getDurationEnd()  > 0){
    	 searchQuery.append(" AND ( Duration_Time between ").append(advanceSearchVO.getDurationStart()).append( " AND  ").append(advanceSearchVO.getDurationEnd()).append(" ) ");
     }
     
     
     if ( advanceSearchVO.getLocationList()  != null &&  advanceSearchVO.getLocationList().length > 0){
    	 searchQuery.append(" AND ( ");
    	 
    	 
    	 for (int i = 0 ; i < advanceSearchVO.getLocationList().length ; i++){
    		 CountryVO location = advanceSearchVO.getLocationList()[i];
    		 
    		 if( i == advanceSearchVO.getLocationList().length -1) {
    		 searchQuery.append("  Country = '" ) .append(location.getCountry()).append("'  ");
    		 
	    		 if(location.getCity().length > 0){
	    			 searchQuery.append(" AND ( ");
	    		 }
    		 for (int j = 0 ; j < location.getCity().length; j++){
    			 if( j == location.getCity().length-1 ){
    				 searchQuery.append("  City = '").append(location.getCity()[j]).append("' )");
    			 }else{
    				 searchQuery.append("  City = '").append(location.getCity()[j]).append("' OR");
    			 }
    		 }
    		 
    		 searchQuery.append(" ) ");
    		 }else{
    			
        		 searchQuery.append("  Country = '" ) .append(location.getCountry()).append("' AND ( ");
        		 
        		 for (int j = 0 ; j < location.getCity().length; j++){
        			 if( j == location.getCity().length-1 ){
        				 searchQuery.append("  City = '").append(location.getCity()[j]).append("' ) ");
        			 }else{
        				 searchQuery.append("  City = '").append(location.getCity()[j]).append("' OR");
        			 }
        		 }
        		     			 
        		 searchQuery.append(" OR " );
    		 } 
    		 
    		 
    	 }
    	 searchQuery.append(" ) "); 
     }
     
	 if(advanceSearchVO.getLevelOfEdu() != null && advanceSearchVO.getLevelOfEdu().length > 0){
		 searchQuery.append(" AND  " );
		 searchQuery.append(" Course_Type in (");
		 for(int i = 0 ; i < advanceSearchVO.getLevelOfEdu().length ; i++ ){
			  if (i == advanceSearchVO.getLevelOfEdu().length -1){
				  searchQuery.append("'").append(advanceSearchVO.getLevelOfEdu()[i]).append("'");
			  }else{
				  searchQuery.append("'").append(advanceSearchVO.getLevelOfEdu()[i]).append("'");
				  searchQuery.append(",");
			  }
		 }
		 searchQuery.append(" ) ");
	 }
	 
	 if(advanceSearchVO.getUserProfole() == 1){
		UserEducation userEdu = getUserInfo(advanceSearchVO.getSessionToken() , con);
		if(userEdu != null &&  userEdu.getEduSystem() != null && userEdu.getEduCountry() != null ){		
			
			String userEduSystemScore = "0";
			if(userEdu.getEduSystemScore() != null && userEdu.getEduSystemScore() != null)
				userEduSystemScore = userEdu.getEduSystemScore().replace("%", "");
			
			searchQuery.append(" AND ( " );
				updateQueryWithEducation(userEdu,searchQuery,countQuery,userEduSystemScore);
			searchQuery.append(" ) " );
		}
	 }
	 
	 searchQuery.append("  order by c_id desc limit ").append( advanceSearchVO.getPageNo() * EducationSystemConstants.NO_OF_RECORD_PER_PAGE ).append(",").append(EducationSystemConstants.NO_OF_RECORD_PER_PAGE);
	 
	 try {
		PreparedStatement statement = con.prepareStatement(searchQuery.toString());
		loadResultCourses(courseList,searchQuery,statement);
		
		
		if(courseList != null && courseList.size() > 0){
			responseData.setStatus("1");
			responseData.setPageNo(advanceSearchVO.getPageNo());
			responseData.setData(courseList);
			responseData.setMessage("Results found for your search criteria.");
		}else{
			responseData.setStatus("0");
			responseData.setPageNo(advanceSearchVO.getPageNo());
			responseData.setData(null);
			responseData.setMessage("No results found. Please make sure your education information is complete. Try again later.");
		}			
		
		
		return responseData;		
	} catch (SQLException e) {
		logger.error(" advanceSearchCourses () : " , e);
	}finally{
		closeConnection(con);
	}
	 
	logger.debug(searchQuery.toString());
	return null;
}
	
	
	
}
