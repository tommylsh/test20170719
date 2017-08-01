package com.maxim.pos.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("branchMasterDao")
public class BranchMasterDao extends HibernateEntityDAO<BranchMaster, Long>  {

    private static final String HQL_FIND_BRANCH_MASTER = "findBranchMaster";

    public static final String HQL_findBranchMasterByCriteria = "findBranchMasterByCriteria";
    public static final String SQL_findBranchLock = "findBranchLock";
    
    public Integer getBranchLock(String branchCode) {
		LogUtils.printLog("SQL_findBranchLock : "+branchCode);
		
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("branchCode", branchCode);
        Integer i = (Integer) getSingleByQueryKey(SQL_findBranchLock, paramMap);
        
		LogUtils.printLog("SQL_findBranchLock {}", i);
		
		return i ;
    }

    public List<BranchMaster> getBranchMasterList() {
    	return getEntityListByQueryKey(HQL_FIND_BRANCH_MASTER);
//        return getList(new PosDaoCmd(HQL_FIND_BRANCH_MASTER), BranchMaster.class);
    }

    public List<BranchMaster> getBranchMasterList(String branchType) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchType", branchType);
    	return getEntityListByQueryKey(HQL_FIND_BRANCH_MASTER, params);
//        return getList(new PosDaoCmd(HQL_FIND_BRANCH_MASTER, params), BranchMaster.class);
    }

    public BranchMaster getBranchMaster(String branchCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("branchCode", branchCode);
    	return (BranchMaster) getSingleByQueryKey(HQL_FIND_BRANCH_MASTER, params);
//        return getSingle(new PosDaoCmd(HQL_FIND_BRANCH_MASTER, params), BranchMaster.class);
    }

    public BranchMaster getById(Long id) {
    	return super.findByKey(id);
//        return getSingle(BranchMaster.class, id);
    }

    public List<BranchMaster> findBranchMasterByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchMasterByCriteria, paramMap);
    	return getEntityListByQueryKey(HQL_findBranchMasterByCriteria, paramMap);
//        return (List<BranchMaster>) getPaginatedListByCriteriaAndType(cmd, paramMap, BranchMaster.class);
    }

    public Long getBranchMasterCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchMasterByCriteria, paramMap);
//        return getSingle(cmd, Long.class);
    	return (Long) getSingleByQueryKey(HQL_findBranchMasterByCriteria, paramMap);
    }

}
