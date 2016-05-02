package com.ff.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.model.CountryDetails;
import com.ff.vo.CountryResultVO;

public class InfoDAOImpl implements InfoDAO{

	Logger logger = LoggerFactory.getLogger(CourseDAOImpl.class);
	private  DBUtil dbUtil ;
	
	@Override
	public CountryResultVO getCountryDetails(String countryCode) {
		
		String countryDetailQuery = " select * from country_details where country_code = ?";
		Connection con = dbUtil.getJNDIConnection();
		CountryResultVO countryResult = new CountryResultVO();
		PreparedStatement statement = null;
		try {
			statement = con.prepareStatement(countryDetailQuery);
			statement.setString(1, countryCode);
			CountryDetails countryDetails = new CountryDetails();
			ResultSet result = statement.executeQuery();
			
			while(result.next()){
				countryDetails.setClimate(result.getString("climate"));
				countryDetails.setCostOfLiving(result.getString("cost_of_living"));
				countryDetails.setCountryFacts(result.getString("country_facts"));
				countryDetails.setCountryId(result.getInt("cd_id"));
				countryDetails.setCountryName(result.getString("country_code"));
				countryDetails.setCountrySafteyRating(result.getString("cntry_safety_rating"));
				countryDetails.setEduRecognation(result.getString("edu_recognition"));
				countryDetails.setEmgHelp(result.getString("Emg_help"));
				countryDetails.setInternationalHealthCover(result.getString("Int_health_cover"));
				countryDetails.setJobPorspect(result.getString("job_prospect"));
				countryDetails.setLivingAndAccomodation(result.getString("living_n_acomd"));
				countryDetails.setShortSkillIndustry(result.getString("short_skilled_industry"));
				countryDetails.setVisa(result.getString("visa"));
				countryDetails.setWorkAndStudyRules(result.getString("work_and_study_rules"));
			}
			
			countryResult.setData(countryDetails);
			countryResult.setMessage("Country information retrived successfully.");
			
			if(countryDetails.getCountryId() > 0)
				countryResult.setStatus("OK");
			else{
				countryResult.setStatus("FAILED");
				countryResult.setMessage("Unable to retrive country info. Country Code not found :" + countryCode);
				}
			
		} catch (SQLException e) {
			logger.error("getCountryDetails () :" , e);
			countryResult.setMessage("Error occured while retriving country information.");
			countryResult.setStatus("FAILED");			
		}finally{
			closeStatement(statement);
		}
		
		return countryResult;
	}

	private void closeStatement(PreparedStatement statement) {
       try {
		if(statement != null)
			   statement.close();
	} catch (SQLException e) {
		logger.error("getCountryDetails () finally block : " , e);
	}
		
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	
	
}
