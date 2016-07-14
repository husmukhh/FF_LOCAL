package com.ff.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ff.model.Country;
import com.ff.model.Session;

public interface InfoService {
	@POST
	@Consumes({"application/xml","application/json","application/x-www-form-urlencoded"})
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/getCountryDetails")
	public Response getCountryDetails(Country country);
}
