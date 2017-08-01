package com.maxim.pos.common.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PosClientUtils;

@Service("posSystemService")
public class PosSystemServiceImpl implements PosSystemService {

//	@Autowired
//	private PollEodControlDao pollEodControlDao;

    @Autowired
    private ApplicationSettingService applicationSettingService;

//    @Autowired
//    private SmbServiceImpl smbService;

    public java.sql.Date getBussinessDate() {
        String offlineStartTime = this.applicationSettingService.getApplicationSettingCodeValue(ApplicationSettingService.APPLICATION_SETTING_CODE_OFFLINE_START_TIME);
        String offlineEndTime = this.applicationSettingService.getApplicationSettingCodeValue(ApplicationSettingService.APPLICATION_SETTING_CODE_OFFLINE_START_TIME);

        if (offlineStartTime == null) {
            offlineStartTime = "18:00";
        }
        if (offlineEndTime == null) {
            offlineEndTime = "17:59";
        }

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -1);
//		java.sql.Date today = DateUtil.getCurrentDate();
//		Timestamp endTime = Timestamp.valueOf(today.toString() + " " +  offlineEndTime);
        return new java.sql.Date(now.getTime().getTime());
    }


//	//@Override
//	public Boolean checkRealTime(BranchScheme branchScheme, Logger logger) {
//		
//		String branchCode = branchScheme.getBranchMaster().getBranchCode();
//
//		PollEodControl control = pollEodControlDao.findLatestPollEodControl(branchCode, "C");
//		
//		Calendar now = Calendar.getInstance();
////		Time currentTime = new Time(now.getTime().getTime());
//		java.sql.Date currentDate = new java.sql.Date(now.getTime().getTime());
//		
//		now.add(Calendar.DATE, -1);
//		java.sql.Date yesterdayDate = new java.sql.Date(now.getTime().getTime());
//
//		java.sql.Date controlDate = yesterdayDate ;
//		if (control != null)
//		{	
//			controlDate = new java.sql.Date(control.getBusinessDate().getTime());
//			
//			if (controlDate.compareTo(currentDate) >= 0)
//			{
//				LogUtils.printLog(logger,"{} Branche POLL_EOD_CONTROL {} found, Real Time Job Skppied",branchCode,controlDate);
//				return Boolean.FALSE;
//			}
//		}
//
//		
//		List<java.util.Date> dates = this.getPosEODBusinessDateList(branchScheme, controlDate, logger);
//		if (dates.size() > 0)
//		{
//			LogUtils.printLog(logger,"{} Branche History Record > {} found, Real Time Job Skppied",branchCode,controlDate);
//			return Boolean.FALSE;
//		}
//		
//		return Boolean.TRUE;
//		
//
////		String offlineStartTimeStr = this.applicationSettingService.getApplicationSettingCodeValue(ApplicationSettingService.APPLICATION_SETTING_CODE_OFFLINE_START_TIME);
////		String offlineEndTimeStr = this.applicationSettingService.getApplicationSettingCodeValue(ApplicationSettingService.APPLICATION_SETTING_CODE_OFFLINE_START_TIME);
////		
////		if (offlineStartTimeStr == null)
////		{
////			offlineStartTimeStr = "18:00";
////		}
////		if (offlineEndTimeStr == null)
////		{
////			offlineEndTimeStr = "17:59";
////		}
////		
////		
//////		LocalTime a= LocalTime.;
////		Time offlineStartTime = Time.valueOf(offlineStartTimeStr);
////		
////		if (now.after(offlineStartTime))
////		{
////			
////		}
//		
//		
//	}


    @Override
    public List<java.util.Date> getPosEODBusinessDateList(BranchScheme branchScheme, java.sql.Date businessDate, Logger logger) {
//		boolean b1 = false;
//		boolean b2 = false;
        List<java.util.Date> dates = new ArrayList<java.util.Date>();

//		Date businessDate = getBussinessDate() ;
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
//		PollEodControl control = pollEodControlDao.findLatestPollEodControl(branchCode, "C");
////		if(b2){
////			return true;
////		}
//		// There are Completed EOD for the the business date
//		// --> No need to Start the EOD
//		if (control != null)
//		{
//			if (control.getBusinessDate().after(businessDate))
//			return false ;
//		}


//		LogUtils.printLog(logger,"{} Branche POLL_EOD_CONTROL not found",branchCode);
//		logger.info("SALES_EOD is : "+b2+" , not found POLL_EOD_CONTROL DATA by "+branchCode);
        try (Connection connection = applicationSettingService.getJDBCConection(branchScheme, true)) {
            String query = "select business_date from hist_possystem where branch_code = '" + branchCode + "' and business_date > '" + businessDate + "' order by business_date asc";
            List<Map<String, Object>> posSystemList = PosClientUtils.execCliectQuery(connection, query, false);
            if (posSystemList.size() > 0 && !posSystemList.isEmpty()) {
                for (Map<String, Object> map : posSystemList) {
                    java.util.Date date = (java.util.Date) map.get("business_date");
                    dates.add(date);
                }
            } else {
                LogUtils.printLog(logger, "{} Branche No hist_possystem > {}", branchCode, businessDate);
//				logger.info("SALES_EOD is : "+b1+" , not found hist_possystem Data by "+ branchCode);
            }
        } catch (SQLException e) {
            logger.error("Connection is Exception Or execute fail:", e);
        }
        return dates;
    }
}