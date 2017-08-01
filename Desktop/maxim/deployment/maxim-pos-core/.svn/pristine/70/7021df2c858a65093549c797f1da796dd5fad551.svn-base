package com.maxim.pos.common.service;

import java.util.List;
import java.util.Map;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.value.CommonCriteria;


public interface PollSchemeInfoService {

	public static final String BEAN_NAME = "pollSchemeInfoService";
	
    public List<SchemeInfo> findSchemeInfoByCriteria(CommonCriteria criteria);

    public List<SchemeInfo> findSchemeInfo(Map<String, Object> paramMap);

    public List<SchemeInfo> findSchemeInfoBySchemeTypeAndClientType(String pollSchemeType, ClientType clientType);
    public List<SchemeInfo> findSchemeInfoByBranchSchemeAndClientType(BranchScheme branchScheme, ClientType clientType) ;

    public SchemeInfo addOrUpdateSchemeInfo(SchemeInfo schemeInfo);
    
	public List<Map<String, Object>> getTableColumnInfo(String tableName);

	public List<SchemeTableColumn> generateSchemeTableColumnData(SchemeInfo schemeInfo);

    void delete(Long schemeInfoId);

    Long getSchemeInfoCountByCriteria(CommonCriteria criteria);

}
