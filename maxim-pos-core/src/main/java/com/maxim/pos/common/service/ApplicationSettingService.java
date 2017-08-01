package com.maxim.pos.common.service;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.value.CommonCriteria;

public interface ApplicationSettingService {

	public static final String APPLICATION_SETTING_CODE_OFFLINE_START_TIME = "OFFLINE_START_TIME";
	public static final String APPLICATION_SETTING_CODE_OFFLINE_END_TIME = "OFFLINE_END_TIME";

	public String getApplicationSettingCodeValue(String code) ;
	
    public ApplicationSetting findApplicationSettingByCode(String code);

    public List<ApplicationSetting> findApplicationSettingByCriteria(CommonCriteria criteria);

    public Long getApplicationSettingCountByCriteria(CommonCriteria criteria);

    public ApplicationSetting saveApplicationSetting(ApplicationSetting applicationSetting);
    
    public void deleteApplicationSettingById(Long applicationSettingId);
    
    public String getCurrentJDBCConnectionString();
    public Connection getCurrentJDBCConnection();
	public Connection getMasterJDBCConnection() ;
	public Connection getEdwJDBCConnection() ;

	public Connection getJDBCConection(BranchScheme scheme, boolean retry);
    public boolean checkConnection(BranchScheme scheme) throws IOException ;

}