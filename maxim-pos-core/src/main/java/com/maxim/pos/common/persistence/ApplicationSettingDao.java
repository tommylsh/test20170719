package com.maxim.pos.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("applicationSettingDao")
@Transactional
public class ApplicationSettingDao extends HibernateEntityDAO<ApplicationSetting, Long>  {

    public static final String HQL_findApplicationSettingByCode = "findApplicationSettingByCode";
    public static final String HQL_findApplicationSettingByCriteria = "findApplicationSettingByCriteria";
    public static final String SQL_findApplicationLock = "findApplicationLock";
    public static final String SQL_findApplicationRptReadLock = "findApplicationRptReadLock";
    public static final String SQL_findApplicationValue = "findApplicationValue";
    
    public static final String MAIN_LOCK = "SCHEDULE";
    

    public ApplicationSetting findApplicationSettingByCode(String code) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("code", code);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findApplicationSettingByCode, paramMap);
        
        Query query = createQueryByQueryKey(HQL_findApplicationSettingByCode, paramMap);

        return (ApplicationSetting) getSingle(query);
    }

    @SuppressWarnings("unchecked")
	public List<ApplicationSetting> findApplicationSettingByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findApplicationSettingByCriteria, paramMap);
//
//        protected List<? extends Object> getPaginatedListByCriteriaAndType(DaoCmd cmd, Map<String, Object> paramMap, Class<?> clazz) {
//            return getList(cmd, clazz, (Integer) paramMap.get(START_FROM_KEY), (Integer) paramMap.get(MAX_RESULT_KEY));
//        }
        return (List<ApplicationSetting>) getListByQueryKey(HQL_findApplicationSettingByCriteria, paramMap);
        
//        return (List<ApplicationSetting>) getPaginatedListByCriteriaAndType(cmd, paramMap, Permission.class);
    }

    public Long getApplicationSettingCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findApplicationSettingByCriteria, paramMap);

        return (Long) getSingleByQueryKey(HQL_findApplicationSettingByCriteria, paramMap);
    }

    public String getApplicationValue(String code) {
    	
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("code", code);
        return (String) getSingleByQueryKey(SQL_findApplicationValue, paramMap);
    }
    
	public int getApplicationLock() {
		return getApplicationLock(MAIN_LOCK);
	}
    public int getApplicationLock(String code) {
		LogUtils.printLog("SQL_findApplicationLock : {} ", code);
		
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        if (!code.equals(MAIN_LOCK))
        {
	        paramMap.put("code", "LOCK");
	        String method = (String) getSingleByQueryKey(SQL_findApplicationValue, paramMap);
	        if (method == null || (!method.equals("SCHEME") && !method.equals("BRANCH_ONLY")))
	        {
	        	code = MAIN_LOCK ; 
	        }
        }

        paramMap.put("code", code);
        Integer value = (Integer) getSingleByQueryKey(SQL_findApplicationLock, paramMap);
        
		LogUtils.printLog("SQL_findApplicationLock : {} - {}", code, value);
		
    	if (value == null)
    	{
    		String codeValue = "LOCK";
    		ApplicationSetting setting = new ApplicationSetting();
    		setting.setCode(code);
    		setting.setCodeDescription("Lock for " + code);
    		setting.setCodeValue(codeValue);
    		Auditer.audit(setting);
    		insert(setting);
    	}
    	
		return 1 ;
    }
 
    public int getApplicationRptReadLock(String code) {
		LogUtils.printLog("SQL_findApplicationRptReadLock : {} ", code);
		
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        paramMap.put("code", code);
        Integer value = (Integer) getSingleByQueryKey(SQL_findApplicationRptReadLock, paramMap);
        
		LogUtils.printLog("SQL_findApplicationRptReadLock : {} - {}", code, value);
		
    	if (value == null)
    	{
    		String codeValue = "LOCK";
    		ApplicationSetting setting = new ApplicationSetting();
    		setting.setCode(code);
    		setting.setCodeDescription("Lock for " + code);
    		setting.setCodeValue(codeValue);
    		Auditer.audit(setting);
    		insert(setting);
    	}
    	
		return 1 ;
    }
 
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public boolean acquireTaskJobLog() 
    {
    	System.out.println("START");
    	getApplicationLock("SALES_REALTIME");
    	
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println("END");
    	return true ;
    }

}
