package com.maxim.pos.sales.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.config.SecurityConfig;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("branchInfoDao")
public class BranchInfoDao extends HibernateDAO {
	
    @Autowired
	SecurityConfig securityConfig ;

    public static final String HQL_findBranchInfoByCriteria = "findBranchInfoByCriteria";

    public BranchInfo getById(Long id) {
        return getSingle(BranchInfo.class, id);
    }

    public List<BranchInfo> findBranchInfoList(Map<String, Object> paramMap) {
        Map<String, Object> param = new HashMap<>(paramMap);
        param.put("queryRecord", Boolean.TRUE);
        return getList(new PosDaoCmd(HQL_findBranchInfoByCriteria, param), BranchInfo.class);
    }

    public List<BranchInfo> findBranchInfoByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchInfoByCriteria, paramMap);
        return (List<BranchInfo>) getPaginatedListByCriteriaAndType(cmd, paramMap, BranchInfo.class);
    }

    public Long getBranchInfoCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchInfoByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

    @Override
    public Object merge(Object obj) {
    	BranchInfo info = (BranchInfo) obj ;
		try {
	    	if (securityConfig.isEncryptUsername())
	    	{
				info.setUser(securityConfig.aesEncrypt(info.getUser()));
	    	}
	    	if (securityConfig.isEncryptPassword())
	    	{
	    		info.setPassword(securityConfig.aesEncrypt(info.getPassword()));
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return entityManager.merge(info);
    }


}
