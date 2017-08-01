package com.maxim.pos.sales.persistence;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.persistence.PosDaoCmd;

@Repository("schemeInfoDao")
public class SchemeInfoDao extends HibernateDAO {
	public static final String HQL_findSchemeInfoBybranchSchemeId = "findSchemeInfoBybranchSchemeId";
	public static final String HQL_findBranchSchemeByPollSchemeType = "findBranchSchemeByPollSchemeType";
	public static final String HQL_findBranchSchemeByPollSchemeTypeAndDirectionAndClientType = "findBranchSchemeByPollSchemeTypeAndDirectionAndClientType";
    public static final String SQL_getTableColumnInfoByDBNameAndTable = "getTableColumnInfoByDBNameAndTable";
    
    public static final String SQL_getLatestPollBrachScheme = "findLatestPollBrachScheme";
    
	
	public List<SchemeInfo> findBySchemeInfoId(Long branchSchemeId) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("brancheSchemeId", branchSchemeId);
		PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeInfoBybranchSchemeId, paramMap);
		return getList(cmd, SchemeInfo.class, (Integer) paramMap.get(START_FROM_KEY),
				(Integer) paramMap.get(MAX_RESULT_KEY));
	}
	
	public List<BranchScheme> findByPollSchemeType(PollSchemeType pollSchemeType,Direction direction){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pollSchemeType", pollSchemeType);
		paramMap.put("direction", direction);
		PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchSchemeByPollSchemeType, paramMap);
		return getList(cmd, BranchScheme.class);
	}

	public BranchScheme findbyPollSchemeTypeAndDirectionAndClientType(PollSchemeType pollSchemeType, Direction direction,ClientType clientType, String branchCode) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pollSchemeType", pollSchemeType);
		paramMap.put("direction", direction);
		if (clientType != null)
		{
			paramMap.put("clientType", clientType);
		}
		paramMap.put("branchCode", branchCode);
		paramMap.put("enabled", true);
		PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchSchemeByPollSchemeTypeAndDirectionAndClientType, paramMap);
		return getSingle(cmd, BranchScheme.class);
	}
	
	public List<Map<String, Object>> findLatestPollBrachScheme(PollSchemeType pollSchemeType, Direction direction) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pollSchemeType", pollSchemeType.name());
		paramMap.put("direction", direction.name());
		PosDaoCmd cmd = new PosDaoCmd(SQL_getLatestPollBrachScheme, paramMap);
		cmd.setDefaultEntityMapTransformer();
		return getList(cmd, Map.class);
	}

}
