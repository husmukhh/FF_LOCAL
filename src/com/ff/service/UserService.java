package com.ff.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ff.model.Session;
import com.ff.model.User;
import com.ff.model.UserEducation;
import com.ff.model.UserInfo;
import com.ff.model.UserInterest;

public interface UserService {

	@POST
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/login")
	public Response login( User user );
	
	@POST
	@Consumes({"application/xml","application/json","application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/registration")
	public Response registration( User user);	
	
	@POST
	@Consumes({"application/xml","application/json","application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/logout")
	public Response logOut(Session sessionToken);
	
	
	@POST
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/userInfo")	
	public Response userInfo(UserInfo userInfo);
	
	@POST
	@Consumes({"application/xml",MediaType.APPLICATION_JSON,"application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/userInterests")
	public Response userInterests(UserInterest userIntrest );
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON  })
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/userEducation")
	public Response userEducation(UserEducation userEducation);	
	
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON  })
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getUserProfile")
	public Response getUserProfile(Session sessionToken);
	
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON  })
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/updateUserProfileStatus")
	public Response updateUserProfileStatus(Session sessionToken);
	
}
