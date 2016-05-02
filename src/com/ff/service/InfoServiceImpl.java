package com.ff.service;

import javax.ws.rs.core.Response;

import com.ff.dao.InfoDAO;
import com.ff.model.Country;

public class InfoServiceImpl implements InfoService{

	private InfoDAO infoDao;
	
	@Override
	public Response getCountryDetails(Country country) {
		return Response.ok(infoDao.getCountryDetails(country.getCountryCode())).build();
	}

	public InfoDAO getInfoDao() {
		return infoDao;
	}

	public void setInfoDao(InfoDAO infoDao) {
		this.infoDao = infoDao;
	}

	
	
}
