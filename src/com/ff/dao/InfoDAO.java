package com.ff.dao;

import com.ff.vo.CountryResultVO;

public interface InfoDAO {
	
	public CountryResultVO getCountryDetails(String countryCode);
}
