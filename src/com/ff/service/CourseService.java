package com.ff.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ff.model.AdvanceSearchReq;
import com.ff.model.Session;
import com.ff.vo.SearchVO;
import com.ff.vo.UnlockCourseVO;

@Produces({"application/json" ,"application/xml"})
@Consumes({"application/xml","application/json","application/x-www-form-urlencoded",MediaType.MULTIPART_FORM_DATA})
public interface CourseService {


	
	
	
	@GET
	@Path("/getCountrySchools/{countryId}")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response getCountrySchools(@PathParam("countryId") int countryId);
	

	@POST
	@Path("/searchCourses")	
	@Produces({MediaType.APPLICATION_JSON})	
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	public Response searchCourses( SearchVO searchVO );
	
	
	
	@POST
	@Path("/unlockCourse")	
	@Produces({MediaType.APPLICATION_JSON})	
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	public Response unlockCourse( UnlockCourseVO unlockCourseVO );
	
	@POST
	@Path("/getUserUnlockedCourses")	
	@Produces({MediaType.APPLICATION_JSON})	
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	public Response getUserUnlockedCourses( Session sessionToken );	
	
	
	@POST
	@Path("/searchCoursesAdvance")	
	@Produces({MediaType.APPLICATION_JSON})	
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	public Response searchCourses( AdvanceSearchReq advanceSearchVO );	
	
	
}



