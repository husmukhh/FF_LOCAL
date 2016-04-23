package com.ff.dao;

import java.sql.SQLException;
import java.util.List;

import com.ff.model.CourseResultVO;
import com.ff.model.School;
import com.ff.model.SearchResultVO;
import com.ff.model.SearchVO;
import com.ff.model.UnlockCourseVO;

public interface CourseDAO {
	public List<School> getAllCountrySchool(int countryId )throws SQLException;

	public SearchResultVO searchCourses(SearchVO searchVO)throws SQLException;

	public CourseResultVO unlockCourse(UnlockCourseVO unlockCourseVO)throws SQLException;
	
	
	
}
