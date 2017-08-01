package com.maxim.pos.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("pollSchemeInfoDao")
public class PollSchemeInfoDao extends HibernateDAO {

    private static final String HQL_findSchemeInfoByCriteria = "findSchemeInfoByCriteria";
    private static final String HQL_findSchemeInfoBySchemeTypeAndClientType = "findSchemeInfoBySchemeTypeAndClientType";
//    private static final String SQL_getTableColumnInfoByDBNameAndTable = "getTableColumnInfoByDBNameAndTable";
    
    public List<SchemeInfo> findSchemeInfoByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//      PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeInfoByCriteria, paramMap);
//      return getList(cmd, SchemeInfo.class);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeInfoByCriteria, paramMap);
        return getList(cmd, SchemeInfo.class,criteria.getStartFrom(),criteria.getMaxResult());
    }

    public List<SchemeInfo> findSchemeInfoBySchemeTypeAndClientType(String pollSchemeType, ClientType clientType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("pollSchemeType", pollSchemeType);
        paramMap.put("clientType", clientType);
        return findSchemeInfo(paramMap);
    }

    public SchemeInfo addOrUpdateSchemeInfo(SchemeInfo schemeInfo) {
        Auditer.audit(schemeInfo);
        return (SchemeInfo) save(schemeInfo);
    }

    public List<SchemeInfo> findSchemeInfo(Map<String, Object> paramMap) {
        return getList(new PosDaoCmd(HQL_findSchemeInfoBySchemeTypeAndClientType, paramMap), SchemeInfo.class);
    }
    
    public List<Map<String, Object>> getTableColumnInfoByDBNameAndTable(String databaseName, String tableName){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("databaseName", databaseName);
        paramMap.put("tableName", tableName);
        return getList(new PosDaoCmd(HQL_findSchemeInfoBySchemeTypeAndClientType, paramMap), HashMap.class);
    }

    public Long getSchemeInfoCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeInfoByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

    public SchemeInfo getById(Long id) {
        return getSingle(SchemeInfo.class, id);
    }
    
}
