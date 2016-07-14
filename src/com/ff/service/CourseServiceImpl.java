package com.ff.service;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ff.dao.CourseDAO;
import com.ff.model.AdvanceSearchReq;
import com.ff.model.School;
import com.ff.model.Session;
import com.ff.service.resp.SchoolResposeList;
import com.ff.vo.CourseResultVO;
import com.ff.vo.SearchResultVO;
import com.ff.vo.SearchVO;
import com.ff.vo.UnlockCourseVO;
import com.ff.vo.UnlockedCourseVO;

public class CourseServiceImpl implements CourseService{
	


	Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
	
	@Context ServletContext context;
	
	
	private CourseDAO courseDAO;
	

	public CourseDAO getCourseDAO() {
		return courseDAO;
	}

	public void setCourseDAO(CourseDAO courseDAO) {
		this.courseDAO = courseDAO;
	}

	@Override
	public Response getCountrySchools(int countryId) {
		SchoolResposeList schoolResponseList = new SchoolResposeList();
		try{
			List<School> schoolList = courseDAO.getAllCountrySchool(countryId);
			
			
			schoolResponseList.setSchoolList(schoolList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		return Response.ok(schoolResponseList).build();
		
	}

	@Override
	public Response searchCourses(SearchVO searchVO) {
		SearchResultVO responseData = null ;
		logger.debug("in searchCourse method.");
		try {
			responseData = courseDAO.searchCourses(searchVO);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.ok(responseData).build();
		 
	}

	@Override
	public Response unlockCourse(UnlockCourseVO unlockCourseVO) {
		CourseResultVO courseResultVO = null;
		try {
			courseResultVO = courseDAO.unlockCourse(unlockCourseVO);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.ok(courseResultVO).build();
	}	

	
	@Override
	public Response getUserUnlockedCourses(Session sessionToken) {
		UnlockedCourseVO unlockedCourseVo = null;
		unlockedCourseVo = courseDAO.getUserUnlockedCourses(sessionToken.getSessionToken());
		return Response.ok(unlockedCourseVo).build();
	}

	@Override
	public Response searchCourses(AdvanceSearchReq advanceSearchVO) {
		SearchResultVO responseData = null ;
		responseData = courseDAO.advanceSearchCourses(advanceSearchVO);
		return null;
	}
	
	
	
	
}
