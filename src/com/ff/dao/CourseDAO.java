package com.ff.dao;

import java.sql.SQLException;
import java.util.List;

import com.ff.model.School;
import com.ff.vo.CourseResultVO;
import com.ff.vo.SearchResultVO;
import com.ff.vo.SearchVO;
import com.ff.vo.UnlockCourseVO;
import com.ff.vo.UnlockedCourseVO;

public interface CourseDAO {
	public List<School> getAllCountrySchool(int countryId )throws SQLException;

	public SearchResultVO searchCourses(SearchVO searchVO)throws SQLException;

	public CourseResultVO unlockCourse(UnlockCourseVO unlockCourseVO)throws SQLException;

	public UnlockedCourseVO getUserUnlockedCourses(String sessionToken);
	
	
	
}
