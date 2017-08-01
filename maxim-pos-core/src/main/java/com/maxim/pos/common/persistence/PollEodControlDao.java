package com.maxim.pos.common.persistence;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.PollEodControl;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PosClientUtils;

@Repository("pollEodControlDao")
public class PollEodControlDao  extends HibernateEntityDAO<PollEodControl, Long>  {
	
	private static final String HQL_findPollEodControlByBranchCode = "findPollEodControlByBranchCode"; 
	private static final String HQL_findCompletedPollEodControlByBranchCode = "findCompletedPollEodControlByBranchCode"; 
	private static final String HQL_findPollEodControlByBranchCodeAfterBusinessDate = "findPollEodControlByBranchCodeAfterBusinessDate"; 
	
	@Autowired
	private ApplicationSettingService applicationSettingService;
	
	public void saveOrUpdateConvertLog(PollEodControl pollEodControl){
		save(pollEodControl);
	}

	public PollEodControl findLatestPollEodControl(String branchCode) {
		
		return findLatestPollEodControl(branchCode, null, null);
	}

	public PollEodControl findLatestPollEodControl(String branchCode, Timestamp minLastUpdateTime) {
		
		return findLatestPollEodControl(branchCode, null, minLastUpdateTime);
	}

	public PollEodControl findLatestPollEodControl(String branchCode, Date maxBusinessDate, Timestamp minLastUpdateTime) {
	    Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("branchCode", branchCode);
        if (minLastUpdateTime != null)
        {
        	paramMap.put("minLastUpdateTime", minLastUpdateTime);
        }
        if (maxBusinessDate != null)
        {
        	paramMap.put("maxBusinessDate", maxBusinessDate);
        }
        
        
		LogUtils.printLog("findLatestPollEodControl paramMap {} ",paramMap  );

        List<PollEodControl> list =  getEntityListByQueryKey(HQL_findPollEodControlByBranchCode, paramMap);

        if(list.size()>0){
	        PollEodControl pollEodControl = list.get(0);
	        if(pollEodControl != null){
	        	return pollEodControl;	
	        }
        }
        return null;
	}

	public PollEodControl findLatestCompletedPollEodControl(String branchCode) {
		return findLatestCompletedPollEodControl(branchCode, null);
	}
	
	public PollEodControl findLatestCompletedPollEodControl(String branchCode, Date maxBusinessDate) {
	    Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("branchCode", branchCode);
        if (maxBusinessDate != null)
        {
        	paramMap.put("maxBusinessDate", maxBusinessDate);
        }
        
        paramMap.put(MAX_RESULT_KEY, 1);

        List<PollEodControl> list =  getEntityListByQueryKey(HQL_findCompletedPollEodControlByBranchCode, paramMap);
        
        if(list.size()>0){
	        PollEodControl pollEodControl = list.get(0);
	        if(pollEodControl != null){
	        	return pollEodControl;	
	        }
        }
        return null;
	}
	
	
	public boolean findConvertLogByBusinessDate(BranchScheme branchScheme, String dateStr) {
//	    BranchInfo branchInfo = branchScheme.getBranchInfo();
	    String branchCode = branchScheme.getBranchMaster().getBranchCode();
		LogUtils.printLog("findConvertLogByBusinessDate {}",branchCode );
	    try(Connection connection = applicationSettingService.getJDBCConection(branchScheme, true)){
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String datetime =sdf.format(new Date());
//	    	String datetime = DateUtil.getCurrentDateString();
	    	String query ="select count(*) as SS from CONVERT_LOG where TO_CHAR(TTDATE,'yyyy-MM-dd') = '"+dateStr+"' and BRNO = '"+branchCode+"'";
			LogUtils.printLog("findConvertLogByBusinessDate query {} ",query  );
	    	List<Map<String, Object>> list = PosClientUtils.execCliectQuery(connection, query, false);
			LogUtils.printLog("findConvertLogByBusinessDate query {} ",query  );
	    	Object obj = list.size() == 0 ? "0" : list.get(0).get("SS");
	    	if(Integer.parseInt(obj.toString()) > 0){
	    		return true;
	    	}
	    }catch (Exception e) {
			LogUtils.printException("select convert_log fail", e);
		}
        return false;
	}
	
	public List<PollEodControl> getPollEodControlListByBranchCodeAfterBusinessDate(String branchCode, Date businessDate) {
	    Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("branchCode", branchCode);
       	paramMap.put("businessDate", businessDate);

        List<PollEodControl> list =  getEntityListByQueryKey(HQL_findPollEodControlByBranchCodeAfterBusinessDate, paramMap);

        return list;
	}


	
//	public boolean findPollEodControlByBranchCodeAndDate(String branchCode) {
//		boolean bl =false;
//		Map<String, Object> paramMap = new HashMap<String,Object>();
//		paramMap.put("branchCode", branchCode);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String datetime =sdf.format(new Date());
//		paramMap.put("businessDate",datetime);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findPollEodControlByBranchCode, paramMap);
//        List<PollEodControl> list = getList(cmd, PollEodControl.class);
//		if(list.size()>0){
//			bl=true;
//		}
//        return bl;
//	}
}
