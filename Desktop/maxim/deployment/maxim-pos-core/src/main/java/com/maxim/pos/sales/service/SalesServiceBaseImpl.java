package com.maxim.pos.sales.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.PollEodControl;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.persistence.PollEodControlDao;
import com.maxim.pos.common.persistence.TaskJobLogDao;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.ProcessStgToEdwService;
import com.maxim.pos.common.service.SchedulerJobLogService;
import com.maxim.pos.common.service.SpringBeanUtil;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.master.persistence.BranchInventoryInfoDao;
import com.maxim.rest.ResponseData;

/**
 * Class SalesServiceBaseImpl
 * <p>
 * Created by Tommy Leung
 * Created on 12 Apr 2017
 * <p>
 * Amendment History
 * <p>
 * Name                  Modified on  Comment
 * --------------------  -----------  ----------------------------------------
 */

public abstract class SalesServiceBaseImpl implements SalesService {


    protected java.sql.Date REALTIME_DATE = java.sql.Date.valueOf("1900-01-01");
    
    protected @Value("${sales.enableArchive}")    				boolean enableArchive;
    protected @Value("${sales.enableBranchCodeMapping}")    	boolean enableBranchCodeMapping;
    protected @Value("${sales.enableWebservice}") 				boolean enableWebservice;
    protected @Value("${sales.enableStockTakeTimeChecking}")	boolean enableStockTakeTimeChecking;
    protected @Value("${sales.proceedEODIfError}")				boolean proceedEODIfError;
//    protected @Value("${sales.enableSuspendRealTimeDuringEOD}") boolean enableSuspendRealTimeDuringEOD;
    
    protected @Value("${sales.fileArchivePath}")  			String salesFileArchivePath = null;
    protected @Value("${sales.stockTakeTableList}")  		String salesStockTakeTableList = null;

    protected @Value("${sales.batchSize}")            				  int defaultTransactionBatchSize;

    protected @Value("${sales.textFileMaxScanDay}")                   int textFileMaxScanDay;
    protected @Value("${sales.textFileDefaultScanDayIfNoControl}")    int textFileDefaultScanDayIfNoControl;
    protected @Value("${sales.sqlMaxScanDay}")                        int sqlMaxScanDay;
    protected @Value("${sales.sqlDefaultScanDayIfNoControl}")         int sqlDefaultScanDayIfNoControl;
    
    protected List<String >stTableList = null ;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @PostConstruct
    public void init() throws Exception {

    	String[] list = StringUtils.split(salesStockTakeTableList,",");
    	
    	stTableList = new ArrayList<String>() ;
    	
    	for (String table : list)
    	{
    		stTableList.add(table.toLowerCase());
    	}

    }

    @Autowired
    private PollSchemeInfoService pollSchemeInfoService;

    @Autowired
    protected ApplicationSettingService applicationSettingService;

    @Autowired
    protected SchedulerJobLogService schedulerJobLogService;

    @Autowired
    protected TaskJobLogService taskJobLogService;

    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    @Autowired
    private RealTimeService realTimeService;
    @Autowired
    private BranchInventoryInfoDao branchInventoryInfoDao;
//    @Autowired
//    private PrcinigMasterService prcinigMasterService;
//    @Autowired
//    private PosSystemService posSystemService;

    @Autowired
    private PollEodControlDao pollEodControlDao;

    @Autowired
    private TaskJobLogDao taskJobLogDao;

    private static String SPLIT_DATE_UPDATE_SQL_PATTERN = 
    		"update {0} set business_date = b.business_date  "
    		+ " from {0} a, hist_orders b "
    		+ " where a.branch_code = b.branch_code "
    		+ " and a.order_no = b.order_no "
    		+ " and a.recall = b.recall "
    		+ " AND a.branch_code = :branchCode " 
    		+ " AND a.business_date = :businessDate " 
    		+ " AND b.business_date > :lastControlDate and b.business_date <= :businessDate " ;
    
    private static String SPLIT_DATE_INSERT_PAYSUM_SQL_PATTERN = " INSERT INTO hist_paysum " +
    		"SELECT branch_code, business_date, station_id, pay_code, cury_no, "+
    		" SUM( tender * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), "+
            " SUM( tender * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), "+
            " exch_rate, "+
            " SUM( pay_amt * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), "+
            " SUM( pay_amt * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), "+
            " SUM( pay_qty * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), "+
            " '', 'P', GETDATE() , NEWID() " +
            " FROM hist_orders_pay  WHERE recall = 0  AND void = '0' " +
    		" AND branch_code = :branchCode " +
    		" AND business_date > :lastControlDate and business_date <= :businessDate " +
            " GROUP BY branch_code, business_date, station_id, pay_code, cury_no, exch_rate " ;

    private static String SPLIT_DATE_DELETE_PAYSUM_SQL_PATTERN = " DELETE FROM hist_paysum " +
    		" WHERE branch_code = :branchCode " +
			" AND business_date > :lastControlDate and business_date <= :businessDate" ;


    private static String SPLIT_DATE_INSERT_POSSYS_SQL_PATTERN = "INSERT INTO hist_possystem \n" +
            "SELECT\n" +
            " branch_code,\''{0}\'',branch_cname,branch_ename,branch_type,register_count,service_chg_rate,gst_rate,\n" +
            " accum_sales,post_count,open_time,post_time,post_mode,net_disc,disc_prod,n_cake_sale,cake_order,cake_redeem,input_sale,\n" +
            " reserved,status,last_update_time,newid() rowguid\n" +
            " FROM hist_possystem (nolock)\n" +
    		" WHERE branch_code = :branchCode " +
    		" AND business_date = :businessDate " ;
    
    private static String SPLIT_DATE_SELECT_PAYCODE_SQL_PATTERN = "SELECT pay_code FROM payment p (NOLOCK) " +
    		" where  p.branch_code = :branchCode "+ 
            " and (p.cury_no = '000' or ( exists (select 1 from currency c where p.cury_no = c.cury_no and p.branch_code = c.branch_code " + 
            " and c.base_cury = '1' and c.reserved <> 'DEL' ))) " +
            " and p.reserved <> 'DEL' " +
            " and p.bank_in = '1' " ;

    private static String SPLIT_DATE_SELECT_BASE_CURRENCY_SQL = "SELECT cury_no FROM currency" +
            " where base_cury = '1' and reserved <> 'DEL' and branch_code = :branchCode " ;
    
    private static String SPLIT_DATE_SELECT_BASE_CURRENCY_FROM_ORDER_PAY_SQL = "SELECT top 1 cury_no FROM hist_orders_pay (nolock) " +
            " where branch_code= :branchCode and pay_code='0001' and business_date=:businessDate " ; 

    
    private static String SPLIT_DATE_SELECT_PAYFIG_SQL_PATTERN = "SELECT TOP 1 * " +
    		" FROM HIST_PAYFIG (NOLOCK) " +
    		" WHERE branch_code = :branchCode " +
    		" AND business_date = :businessDate " +
    		" AND type like 'B%' and void = 0 " ;

    private static String SPLIT_DATE_DELETE_PAYFIG_SQL_PATTERN = "DELETE HIST_PAYFIG " +
    		" WHERE branch_code = :branchCode " +
    		" AND business_date > :lastControlDate and business_date <= :businessDate" +
    		" AND type like 'B%' and void = 0 " ;

    private static String SPLIT_DATE_SELECT_ORDERPAY_SQL_PATTERN = "SELECT business_date, cury_no, pay_code, " +
    		" ISNULL(SUM( (tender + tips) * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), 0) as amt, " +
    		" ISNULL(SUM( (pay_amt + change) * case when refund = '0' then 1 when refund = '1' then -1 else 0 end), 0) as local_amt, " +
    		" ISNULL(SUM(change * case when refund = '0' then -1 when refund = '1' then 1 else 0 end), 0) as change" +
    		" FROM hist_orders_pay (NOLOCK) " + 
    		" WHERE recall = 0 AND void = '0' " +
    		" AND branch_code = :branchCode " +
//    		" AND business_date > :lastControlDate and business_date <= :businessDate " +
    		" AND business_date = :splitDate " +
    		" GROUP BY business_date, cury_no, 	pay_code" ;
    
    private static String SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN = "INSERT INTO HIST_PAYFIG values(" +
    		" :branch_code, :splitDate, :trans_datetime, :uid, :station_id, :type, :remarks, " +
    		" :cury_no, :input_amt, 0, :local_amt, cast(right(convert(char(8),getdate(), 108), 1)as int), " +
    		" :void, :void_datetime, :void_uid, :void_station_id, :reserved, :status, getdate(), newid() )"; 

//    private static String SPLIT_DATE_UPDATE_PAYFIG_SQL_PATTERN = "UPDATE HIST_PAYFIG " + 
//            " SET type = :type, input_amt = :amt, local_amt= :local_amt, last_update_time = getdate() " +
//    		" WHERE branch_code = :branch_code " +
//    		" AND business_date = :businessDate " +
//    		" AND type like 'B%' and void = 0 " ;


    private static String SPLIT_DATE_UPDATE_PAYFIG_A_SQL_PATTERN = "UPDATE hist_payfig " +
    		" set input_amt = a.input_amt + b.input_amt, local_amt = a.local_amt + b.local_amt " +
    		" from hist_payfig a, " + 
    		" (select cury_no, sum(input_amt) as input_amt, sum(local_amt) as local_amt " +
    		" from hist_payfig where business_date = :splitDate " +
    		" and branch_code = :branchCode and type like 'A%' and void = 0 group by cury_no) b " +
    		" where a.cury_no = b.cury_no " +
    		" AND a.branch_code = :branchCode " +
    		" AND a.business_date = :splitDate "  +
    		" AND type like 'B%' ";
    private static String SPLIT_DATE_UPDATE_PAYFIG_E_SQL_PATTERN = "UPDATE hist_payfig " +
    		" set input_amt = a.input_amt + b.input_amt, local_amt = a.local_amt + b.local_amt " +
    		" from hist_payfig a, " + 
    		" (select cury_no, sum(input_amt) as input_amt, sum(local_amt) as local_amt " +
    		" from hist_payfig where business_date = :splitDate " +
    		" and branch_code = :branchCode and type like 'E%' and void = 0 group by cury_no) b " +
    		" where a.cury_no = b.cury_no " +
    		" AND a.branch_code = :branchCode " +
    		" AND a.business_date = :splitDate "  +
    		" AND type like 'B%' ";
    private static String SPLIT_DATE_UPDATE_PAYFIG_S_SQL_PATTERN = "UPDATE hist_payfig " +
    		" set input_amt = a.input_amt - b.input_amt, local_amt = a.local_amt - b.local_amt " +
    		" from hist_payfig a, " + 
    		" (select cury_no, sum(input_amt) as input_amt, sum(local_amt) as local_amt " +
    		" from hist_payfig where business_date = :splitDate " +
    		" and branch_code = :branchCode and type like 'S%' and void = 0 group by cury_no) b " +
    		" where a.cury_no = b.cury_no " +
    		" AND a.branch_code = :branchCode " +
    		" AND a.business_date = :splitDate "  +
    		" AND type like 'B%' ";
    
//    
//    @Autowired
//    private SchemeScheduleJobService schemeScheduleJobService;
//    
//    @Autowired
//    private SchemeJobLogDao schemeJobLogDao;

    @Override
    public String processPosDataToStg(BranchScheme branchScheme, Logger logger) {

        return processPosDataToStg(branchScheme, null, logger);
    }

    @Override
    public String processPosDataToStg(BranchScheme branchScheme, SchemeJobLog schemeJobLog, Logger logger) {

        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        ClientType clientType = branchScheme.getBranchInfo().getClientType();
        PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
//        String pollSchemeName = branchScheme.getPollSchemeName();
//		String pollSchemeType = branchScheme.getPollSchemeType().name()==branchScheme.getPollSchemeName()
//				? branchScheme.getPollSchemeType().name():branchScheme.getPollSchemeName();

        LogUtils.printLog(logger, "{} {} processPosDataToStg {} {}", branchCode, pollSchemeType, branchScheme.isReRun(), branchScheme.getBusinessDate());

        boolean start = false;                                // Indicate the job whether it is start
        boolean stage = false;                                // Indicate the job whether it is start
        boolean done = false;                                // Indicate the job whether it is done
//        boolean skipped = false;                             // Indicate the job whether it is skppied
//        String  errorMessage = "";                           // The job Error Message
        java.sql.Date controlDate = null;                    // Last EOD run Date
        List<Date> prcDates			= new ArrayList<Date>();        // The Date going to process
        List<Date> stDates  		= new ArrayList<Date>();        // The Date going to stock Take
        List<Date> nonStDoneDates	= new ArrayList<Date>();        // The Date that non-stock take is done.
        List<Date> stDoneDates		= new ArrayList<Date>();        // The Date that stock is done
        List<Date> stReady 			= new ArrayList<Date>();        // The Date that stock is ready
        List<Date> readyDates		= new ArrayList<Date>();        // The Date that stock is ready
        TaskJobLog taskLog = branchScheme.getTaskLog();    // The task Log (Scheduling will assign / Re-run will not assign yet)
        List<PollEodControl> eodControls = null;
        if (branchScheme.isReRun() && branchScheme.getDirection() == Direction.STG_TO_EDW) {
            Calendar c = Calendar.getInstance();
            c.setTime(branchScheme.getBusinessDate());
            c.add(Calendar.DATE, -1);
            controlDate = new java.sql.Date(c.getTime().getTime());
            controlDate = java.sql.Date.valueOf(controlDate.toString());
            prcDates.add(branchScheme.getBusinessDate());
        }

        // For POS -> Staging 
        if(!branchScheme.isReRun() || branchScheme.getDirection()==Direction.POS_TO_STG)
        {
        	// If Re-run, there are no task log assign by scheduler, the systme need to accquire
        	taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, schemeJobLog);
	        if (taskLog == null) {
	            LogUtils.printLog(logger, "{} {} processPosDataToStg End (Exist Processing Task)", branchCode, pollSchemeType);
	            return null;
	        }
	
	        LogUtils.printLog(logger, "{} {} processPosDataToStg task id {} / last task id {} ", branchCode, pollSchemeType, 
	        		taskLog.getId(), taskLog.getLastTaskJobLog()==null?null:taskLog.getLastTaskJobLog().getId());
	        
	        Calendar now = Calendar.getInstance();
	        java.sql.Date currentDate = new java.sql.Date(now.getTime().getTime());
	        currentDate = java.sql.Date.valueOf(currentDate.toString());
	        now.add(Calendar.DATE, -1);
	        java.sql.Date yesterdayDate = new java.sql.Date(now.getTime().getTime());
	        yesterdayDate = java.sql.Date.valueOf(yesterdayDate.toString());
	        try {
	            if (branchScheme.getBusinessDate() == null) {
	                PollEodControl control = branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD ?
	                			pollEodControlDao.findLatestPollEodControl(branchCode) :
		                		pollEodControlDao.findLatestCompletedPollEodControl(branchCode) ;
	                			
	                if (control != null) {
	                    controlDate = new java.sql.Date(control.getBusinessDate().getTime());
	                    controlDate = java.sql.Date.valueOf(controlDate.toString());
	                }
                    LogUtils.printLog(logger, "{} {} processPosDataToStg controlDate{} current{} yesterday{}", branchCode, pollSchemeType, 
                    		controlDate, currentDate, yesterdayDate);
//                    LogUtils.printLog(logger, "{} {} processPosDataToStg controlDate{} current{} yesterday{}", branchCode, pollSchemeType, 
//                    		controlDate == null ? null : controlDate.getTime(), currentDate.getTime(), yesterdayDate.getTime());

                    if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_REALTIME) {
                        if (controlDate == null) {
                            controlDate = yesterdayDate;
                        }
//	                    if (controlDate.compareTo(currentDate) >= 0) {
//	            	        LogUtils.printLog(logger, "{} {} POLL_EOD_CONTROL {} found, Real Time Job Skppied ", branchCode, pollSchemeType, controlDate);
//	                        return null;
//	                    }
                    }
                    if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
                        if (controlDate == null) {
                            now.add(Calendar.DATE, -1 * getDefaultScanDayIfNoControl());
                            controlDate = new java.sql.Date(now.getTime().getTime());
                            controlDate = java.sql.Date.valueOf(controlDate.toString());
                        } else {
                            if (controlDate.compareTo(currentDate) >= 0) {
                                LogUtils.printLog(logger, "{} {} POLL_EOD_CONTROL {} found, EOD Job Skppied", branchCode, pollSchemeType, controlDate);
                                return null;
                            }
                        }
                    }
                } else {

                	PollEodControl control = pollEodControlDao.findLatestCompletedPollEodControl(branchCode, branchScheme.getBusinessDate()) ;
	                if (control != null) {
	                    controlDate = new java.sql.Date(control.getBusinessDate().getTime());
	                    controlDate = java.sql.Date.valueOf(controlDate.toString());
	                }
                    if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_REALTIME) {
                        if (controlDate == null) {
                          Calendar c = Calendar.getInstance();
                          c.setTime(branchScheme.getBusinessDate());
                          c.add(Calendar.DATE, -1);
                          controlDate = new java.sql.Date(c.getTime().getTime());
                        }
                        controlDate = java.sql.Date.valueOf(controlDate.toString());
                    }
                    if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
                        if (controlDate == null) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(branchScheme.getBusinessDate());
                            c.add(Calendar.DATE, -1 * getDefaultScanDayIfNoControl());
                            controlDate = new java.sql.Date(c.getTime().getTime());
                            controlDate = java.sql.Date.valueOf(controlDate.toString());
                        } else {
                            if (controlDate.compareTo(currentDate) >= 0) {
                                LogUtils.printLog(logger, "{} {} POLL_EOD_CONTROL {} found, EOD Job Skppied", branchCode, pollSchemeType, controlDate);
                                return null;
                            }
                        }
                    }
                }
                List<SchemeInfo> schemeList = pollSchemeInfoService.findSchemeInfoByBranchSchemeAndClientType(branchScheme, clientType);

                Calendar scanDaysBeforeCal = Calendar.getInstance();
                scanDaysBeforeCal.setTime(currentDate);
                scanDaysBeforeCal.add(Calendar.DATE, -1 * getMaxScanDay() - 1);

                List<Date> chkDates = doGetPosProcessDate(branchScheme, schemeList, currentDate, yesterdayDate, controlDate, logger);
                
                List<PollEodControl> controlList = new ArrayList<>() ;
                if (!branchScheme.isReRun())
                {
                	List<Date> chkStDates = branchInventoryInfoDao.getPosEODStockDateList(branchScheme.getBranchMaster().getBranchCode(), controlDate);
	                for (Date date : chkStDates)
	                {
	                	if (chkDates.contains(date))
	                	{
	                		stDates.add(date);
	                	}
	                }
	                LogUtils.printLog(logger,"{} {} chkStDates {} stDates {}", branchCode, pollSchemeType, chkDates, stDates);                	
	                
                	controlList = pollEodControlDao.getPollEodControlListByBranchCodeAfterBusinessDate(branchCode, controlDate);
	                if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
	    	            
	                	for (PollEodControl control : controlList)
	                	{
	                		if (control.getStatus().equals("P"))
	                		{
	                			chkDates.remove(control.getBusinessDate());
	                		}
	                		boolean contains = false ;
	                		if (!stDates.contains(control.getBusinessDate()))
	                		{
	                			for (Date date : stDates)
	                			{
	    	                		java.sql.Date date1 = new java.sql.Date(date.getTime());
	    	                		java.sql.Date date2 = new java.sql.Date(control.getBusinessDate().getTime());
	                				if (date1.toString().equals(date2.toString()))
	                				{
	                					contains = true;
	                					break ;
	                				}
	                			}
	                		}
	                		else
	                		{
	                			contains = true;
	                		}
	                		if (contains)
	                		{
		                		if (control.getStatus().equals("S"))
		                		{
		                			nonStDoneDates.add(control.getBusinessDate());
		                		}
		                		if (control.getStatus().equals("T"))
		                		{
		                			stDoneDates.add(control.getBusinessDate());
		                		}
	                		}
	                	}
	                }
                }
                
                
                
//	            if (branchScheme.getBusinessDate() == null) {
//	            	// Not - Run, Need to check Pcoess Date
//	                chkDates = doGetPosProcessDate(branchScheme, schemeList, currentDate, yesterdayDate, controlDate, logger);
//	            } else {
//	            	// Re-Run --> No need Get Process Date
//	                chkDates = new ArrayList<>();
//	                chkDates.add(branchScheme.getBusinessDate());
//	            }
                List<Date> fullPrcDate = new ArrayList<Date>();
	            if (chkDates != null) {
	                for (Date date : chkDates) {
	                    if (branchScheme.isReRun() ||  getMaxScanDay() < 0 || date.equals(REALTIME_DATE) || date.after(scanDaysBeforeCal.getTime())) {
                    		prcDates.add(date);
	                    	fullPrcDate.add(date);
//	                        if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
//	                        	if (!stDates.contains(date))
//	                        	{
//	                        		prcDates.add(date);
//	                        	}
//	                        	else if (stReady.contains(date))
//	                        	{
//	                        		prcDates.add(date);
//	                        	}
//	                    	}
//	                    	else
//	                    	{
//	                    		prcDates.add(date);
//	                    	}
	                    }
	                }
	            } else {
	                return null;
	            }
	            LogUtils.printLog(logger, " {} {} scanDaysControl {} {} -> {}", branchCode, pollSchemeType, 
	            		scanDaysBeforeCal.getTime(), chkDates, prcDates);
	            chkDates = null;
	
	            int realCount = prcDates.contains(REALTIME_DATE) ? 1 : 0 ;
	            		
	            if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType)) {
	                if (prcDates.size() > realCount) {
	                	taskLog.setErrorMsg("Skipped due to EOD not Completed");
                        LogUtils.printLog(logger, "{} {} Branche History Record > {} found, Real Time Job Skppied", 
                        		branchCode, pollSchemeType, controlDate);
                        if (controlList != null)
                        {
		                	for (PollEodControl control : controlList)
		                	{
		                		if (control.getStatus().equals("P"))
		                		{
		                			prcDates.remove(control.getBusinessDate());
		                		}
		                		if (control.getStatus().equals("E"))
		                		{
		                			prcDates.remove(control.getBusinessDate());
		                		}
		                	}
                        }
//    	                PollEodControl last = pollEodControlDao.findLatestPollEodControl(branchCode);
//    	                
//    	                java.sql.Date lastDate = null ;
//    	                
//                        if (last == null) {
//                        	
//                	        now = Calendar.getInstance();
//                            now.add(Calendar.DATE, -1 * getDefaultScanDayIfNoControl());
//                            lastDate = new java.sql.Date(now.getTime().getTime());
//                            lastDate = java.sql.Date.valueOf(controlDate.toString());
//                        } else {
//        	                lastDate = new java.sql.Date(last.getBusinessDate().getTime());
//        	                lastDate = java.sql.Date.valueOf(lastDate.toString());
//                        }
//    	                java.sql.Date lastPrcDate = new java.sql.Date(prcDates.get(prcDates.size()-1).getTime()) ;
//    	                lastPrcDate = java.sql.Date.valueOf(lastPrcDate.toString());
//    	                
//    	                if (lastPrcDate.after(lastDate))
    	                if (prcDates.size() > realCount)
    	                {
    	                	schedulerJobLogService.higherEodPriority(branchCode);
    	                }
	                    return null;
	                }
	                if (realCount == 0)
	                {
                        LogUtils.printLog(logger, "{} {} No Real Time Data found [controlDate :{}], Real Time Job Skppied", 
                        		branchCode, pollSchemeType, controlDate);

                        if (StringUtils.isBlank(taskLog.getErrorMsg()))
                        {
                        	taskLog.setErrorMsg("No Real Time Data found");
                        }
	                	return null;
	                }
	
	            } else if (PollSchemeType.SALES_EOD.equals(pollSchemeType)) {
	
	                if (prcDates.isEmpty()) {
	                    LogUtils.printLog(logger, "{} {} Branch POS EOD IS NOT COMPLETED , SALES_EOD PROCESS WILL BE SKPPIED..."
	                            ,branchCode, pollSchemeType);
	                	taskLog.setErrorMsg("EOD IS NOT COMPLETED");
	                    return null;
	                }
	                if (!stDates.isEmpty())
	                {
		                stReady = doGetStockTakeReadyDate(stDates, branchScheme, schemeList, currentDate, yesterdayDate, controlDate, logger);
		                
		                LogUtils.printLog(logger,"{} {} doGetStockTakeReadyDate prcDates{} stReady {} stDates{}", branchCode, pollSchemeType, prcDates, stReady, stDates);                	
		                
		                for (Date date : prcDates)
		                {
			                LogUtils.printLog(logger,"{} {} doGetStockTakeReadyDate stDates.contains(date) {} stReady.contains(date) {}", branchCode, pollSchemeType, stDates.contains(date), stReady.contains(date));                	
		                	date = new java.sql.Date(date.getTime());
		                	date = java.sql.Date.valueOf(date.toString());
			                LogUtils.printLog(logger,"{} {} doGetStockTakeReadyDate2 stDates.contains(date) {} stReady.contains(date) {}", branchCode, pollSchemeType, stDates.contains(date), stReady.contains(date));                	
		                	if (stDates.contains(date))
		                	{
		                		if (stReady.contains(date))
		                		{
			                		readyDates.add(date);
		                		}
		                	}
		                	else
		                	{
		                		readyDates.add(date);
		                	}
		                }
		                LogUtils.printLog(logger,"{} {} doGetStockTakeReadyDate readyDates {}", branchCode, pollSchemeType, readyDates);
		                if (readyDates.isEmpty())
		                {
		                    LogUtils.printLog(logger, "{} {} Branch POS Stock Take IS NOT COMPLETED , SALES_EOD PROCESS WILL BE SKPPIED..."
		                            ,branchCode, pollSchemeType);
		                	taskLog.setErrorMsg("EOD Stock Take IS NOT COMPLETED");
		                    return null;
		                }
		             }
	                else
	                {
	                	readyDates = prcDates ;
	                }
	            }
	
	            taskLog = taskJobLogService.startTaskJobLog(branchScheme,taskLog);
	            start = true;
	            
	            if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
	                LogUtils.printLog(logger, "{} {} processPosDataToStg insertPollEodControl {} ", branchCode, pollSchemeType, prcDates);
	                eodControls = insertPendingPollEodControl(branchCode, prcDates, controlList);
	            }
	            prcDates = doProcessPosDataToStg(branchScheme, schemeList, prcDates, stDates, nonStDoneDates, stDoneDates, readyDates, currentDate, yesterdayDate, controlDate, taskLog, logger);
	            
	            List<PollEodControl>  waitStEodControls = new ArrayList<PollEodControl>();
	            List<PollEodControl>  readyEodControls = new ArrayList<PollEodControl>();
	            if (PollSchemeType.SALES_EOD.equals(pollSchemeType) && prcDates != null)
	            {
	            	if (taskLog.getStatus().equals(TaskProcessStatus.FAILED))
	            	{
		            	if (!proceedEODIfError)
		            	{
		            		prcDates = null ;
		            	}
	            	}
	            	if (prcDates != null)
	            	{
			            for (Date date : prcDates)
			            {
			            	for (PollEodControl eod : eodControls)
			            	{
			            		if (date.equals(eod.getBusinessDate()))
			            		{
			    	            	if (readyDates.contains(date))
			    	            	{
			    	            		readyEodControls.add(eod);
			    	            	}
			    	            	else
			    	            	{
			    	            		waitStEodControls.add(eod);
			    	            	}
			            		}
			            		break ;
			            	}
			            }
	            	}
	            }
                if (prcDates != null && branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
                	
		            ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("CUT_OFF_TIME");
		            String cutOffTime = applicationSetting == null ? "03:00:00" : applicationSetting.getCodeValue();

                    Set<Date> processDates = new TreeSet<Date>(readyDates);
//                    Set<String> splitDates = new TreeSet<String>();
                    java.sql.Date lastControlDate = new java.sql.Date(controlDate.getTime());
//                    try (Connection connection = applicationSettingService.getCurrentJDBCConnection()) {
                        for (Date date : readyDates) {
                            java.sql.Date businessDate = new java.sql.Date(date.getTime());
                            businessDate = java.sql.Date.valueOf(businessDate.toString());
                            
                            // lotic  revise split date
                            int splitDateCount = this.updateBussinessDate(branchCode, "hist_orders", cutOffTime, lastControlDate, businessDate, logger);
                            LogUtils.printLog(logger, " {} {} hist_orders splitDateCount :{}"
                                    , branchCode, pollSchemeType, splitDateCount);
                            if(splitDateCount!=0){
	                            List<Date> splitDates = getBussinessDate(branchCode, "hist_orders", lastControlDate, businessDate,logger);
	                            processDates.addAll(splitDates) ;
//	                            prcDates = new ArrayList<Date>(processDates);
	                            Map<String, Object> paramMap = new HashMap<String, Object>();
	                            paramMap.put("branchCode", branchCode);
	                            paramMap.put("lastControlDate", lastControlDate);
	                            paramMap.put("businessDate", businessDate);            
	                            
	//                            List<Map<String, Object>> orders = getSplitOrders(branchCode, lastControlDate,businessDate, logger);
	//                            String updatePattern = "update {0} set business_date = \''{1}\'' where branch_code=\''{2}\'' and convert(varchar(10),business_date,120) =\''{3}\'' and recall = {4} and order_no=\''{5}\''";
	//                            String update;
	//                            String orderKeyPattern = "{},{},{},{},";
	//                            String orderKey;
	//                            for (Map<String, Object> map : orders) {
	                                for (SchemeInfo schemeInfo : schemeList) {
	                                    if (schemeInfo.isSplitDateRequired()) {
	                                    	String table = schemeInfo.getDestination() ; 
	                                        try {
	                                            if(StringUtils.endsWithIgnoreCase(table,"hist_orders")){
	                                                continue;
	                                            }
	                                            String sql = MessageFormat.format(SPLIT_DATE_UPDATE_SQL_PATTERN,
	                                                    table); 
	                                            splitDateCount =  jdbcTemplate.update(sql, paramMap);
	                                            LogUtils.printLog(logger, " {} {} {} splitDateCount :{} ,{}", branchCode, pollSchemeType, table, splitDateCount, sql);
	//                                            orderKey =   MessageFormat.format(orderKeyPattern, map.get("business_date"),map.get("branch_code"),map.get("recall"),map.get("order_no"));
	//                                            if (!splitDates.contains(orderKey)) {
	//                                                splitDates.add(orderKey);
	//                                            }
	                                        }catch (Exception e ){
	                                            LogUtils.printLog(logger,"{} split business date exception:{}", table,e.getMessage());
	                                        }
	                                    }
	                                }
	//                            }
	
	                                
	//                            String insertHistPaySumSql = MessageFormat.format(SPLIT_DATE_INSERT_PAYSUM_SQL_PATTERN,  lastControlDate, businessDate);
	//                            int insertPaySumCount = PosClientUtils.updateTable(connection, insertHistPaySumSql);
	
	                            int deletePaySumCount = jdbcTemplate.update(SPLIT_DATE_DELETE_PAYSUM_SQL_PATTERN, paramMap);
	                            LogUtils.printLog(logger, "{} {} insert hist_paysum count{}---{}", branchCode, pollSchemeType, deletePaySumCount, SPLIT_DATE_DELETE_PAYSUM_SQL_PATTERN);
	                                
	                            int insertPaySumCount = jdbcTemplate.update(SPLIT_DATE_INSERT_PAYSUM_SQL_PATTERN, paramMap);
	                            LogUtils.printLog(logger, "{} {} insert hist_paysum count{}---{}", branchCode, pollSchemeType, insertPaySumCount, SPLIT_DATE_INSERT_PAYSUM_SQL_PATTERN);
	
	                            TreeSet<String> payCodeList = new TreeSet<String>(jdbcTemplate.queryForList(SPLIT_DATE_SELECT_PAYCODE_SQL_PATTERN, paramMap, String.class));
	                            if (payCodeList.isEmpty())
	                            {
	                                payCodeList.add("0001");
	                                payCodeList.add("0197");
	                                payCodeList.add("0145");
	                                payCodeList.add("0363");
	                                payCodeList.add("0238");
	                                payCodeList.add("0239");
	                                payCodeList.add("0240");
	                                payCodeList.add("0246");
	                                payCodeList.add("0284");
	                                payCodeList.add("0236");
	                            }
	                            
	
	                            LogUtils.printLog(logger, "{} {} isplitDates payCodeList count{}---{}", branchCode, pollSchemeType, splitDates, payCodeList);
	
	                            Map<String, Object> payFigBaseMap = null ;
	                            try
	                            {
	                            	payFigBaseMap = jdbcTemplate.queryForMap(SPLIT_DATE_SELECT_PAYFIG_SQL_PATTERN, paramMap);
	                                
	                                String remark = (String) payFigBaseMap.get("remarks");
	                                payFigBaseMap.put("remarks", remark.trim() + "-AutoSplit");
	                            }
	                            catch (EmptyResultDataAccessException e) {  }
	                            LogUtils.printLog(logger, "{} {} payFigBaseMap {}", branchCode, pollSchemeType, payFigBaseMap);
	
	                            String baseCcyNo = null ;
	                            try
	                            {
	                            	baseCcyNo = jdbcTemplate.queryForObject(SPLIT_DATE_SELECT_BASE_CURRENCY_SQL, paramMap, String.class);
	                            }
	                            catch (EmptyResultDataAccessException e) {  }
	                            LogUtils.printLog(logger, "{} {} baseCcyNo {} {}", branchCode, pollSchemeType, baseCcyNo, SPLIT_DATE_SELECT_BASE_CURRENCY_SQL);
	
	                            try
	                            {
	                            	baseCcyNo = jdbcTemplate.queryForObject(SPLIT_DATE_SELECT_BASE_CURRENCY_FROM_ORDER_PAY_SQL, paramMap, String.class);
	                            }
	                            catch (EmptyResultDataAccessException e) {  }
	                            LogUtils.printLog(logger, "{} {} baseCcyNo {} {}", branchCode, pollSchemeType, baseCcyNo, SPLIT_DATE_SELECT_BASE_CURRENCY_FROM_ORDER_PAY_SQL);
	
	                            int deletePayFigCount = jdbcTemplate.update(SPLIT_DATE_DELETE_PAYFIG_SQL_PATTERN, paramMap);
	                            LogUtils.printLog(logger, "{} {} insert hist_paysum count{}---{}", branchCode, pollSchemeType, deletePayFigCount, SPLIT_DATE_INSERT_PAYSUM_SQL_PATTERN);
	
	
	//                            String hist_possystem_pattern = "INSERT INTO hist_possystem \n" +
	//                                    "SELECT\n" +
	//                                    "\tbranch_code,\''{0}\'' business_date,branch_cname,branch_ename,branch_type,register_count,service_chg_rate,gst_rate,\n" +
	//                                    "\taccum_sales,post_count,open_time,post_time,post_mode,net_disc,disc_prod,n_cake_sale,cake_order,cake_redeem,input_sale,\n" +
	//                                    "\treserved,status,last_update_time,newid() rowguid\n" +
	//                                    "FROM\n" +
	//                                    "\thist_possystem (nolock)\n" +
	//                                    "WHERE\n" +
	//                                    "\tbusiness_date = \''{1}\''\n" +
	//                                    "AND branch_code = \''{2}\''";
	
	//                            String paySumInsertPattern = "" +
	//                                    " INSERT INTO hist_paysum " +
	//                                    " SELECT sr.branch_code,sr.business_date,sr.station_id,sr.pay_code,sr.cury_no," +
	//                                    " SUM (sr.cury_total_amt) cury_total_amt,SUM (sr.cury_org_amt) cury_org_amt,sr.exch_rate," +
	//                                    " SUM (sr.total_amt) total_amt,SUM (sr.org_amt) org_amt,SUM (sr.total_qty) total_qty"
	//                                    + ",'' reserved,'P' status,GETDATE() last_update_time,NEWID() rowguid\n" +
	//                                    " FROM" +
	//                                    " (SELECT branch_code,business_date,station_id,pay_code,cury_no," +
	//                                    " SUM (tender) cury_total_amt,SUM (tender) cury_org_amt,exch_rate,SUM (pay_amt) total_amt,SUM (pay_amt) org_amt,SUM (pay_qty) total_qty " +
	//                                    " FROM hist_orders_pay" +
	//                                    " WHERE recall = 0 AND refund = '0' AND void = '0' AND business_date = '{0}'\n" +
	//                                    " GROUP BY branch_code,business_date,station_id,pay_code,cury_no,exch_rate\n" +
	//                                    " UNION ALL\n" +
	//                                    " SELECT branch_code,business_date,station_id,pay_code,cury_no,SUM (tender) * - 1 cury_total_amt,SUM (tender) * - 1 cury_org_amt,exch_rate,SUM (pay_amt) * - 1 total_amt,SUM (pay_amt) * - 1 org_amt,SUM (pay_qty) * - 1 total_qty\t\t\t\n" +
	//                                    "FROM\n" +
	//                                    " hist_orders_pay\n" +
	//                                    " WHERE \n" +
	//                                    " recall = 0 AND refund = '1' AND void = '0' AND convert(varchar(10),business_date,120) = \''{0}\''\n" +
	//                                    " AND order_no=\''{1}\''"+
	//                                    " GROUP BY branch_code,business_date,station_id,pay_code,cury_no,exch_rate\n" +
	//                                    "\t) sr\n" +
	//                                    "GROUP BY sr.branch_code,sr.business_date,sr.station_id,sr.pay_code,sr.cury_no,sr.exch_rate;\n" +
	//                                    "\n";
	//                            Iterator<String> it = splitDates.iterator();
	//                            String insertHistPossyste;
	//                            String insertHistPaySum;
	                            // MessageFormat.format(orderKeyPattern, map.get("business_date"),map.get("branch_code"),map.get("recall"),map.get("order_no"));
	//                            String bizDate;
	//                            String branch_code;
	//                            String recall;
	//                            String orderNo;
	//                            String[] orderKeys;
	                            for(Date splitedDate : splitDates){
	                                try {
	//                                    orderKeys = it.next().split(",");
	//                                    bizDate = orderKeys[0];
	//                                    branch_code = orderKeys[1];
	//                                    recall = orderKeys[2];
	//                                    orderNo = orderKeys[3];
	                                	
	                                	java.sql.Date splitSqlDate = new java.sql.Date(splitedDate.getTime());
	                                	splitSqlDate = java.sql.Date.valueOf(splitSqlDate.toString());
	                                    
	                                    paramMap.put("splitDate", splitedDate);
	
	                                    if (splitedDate.before(businessDate))
	                                    {
	                                        String insertHistPossystem = MessageFormat.format(SPLIT_DATE_INSERT_POSSYS_SQL_PATTERN,splitSqlDate.toString());
	                                        int insertHistPossystemCount = jdbcTemplate.update(insertHistPossystem, paramMap);
	                                        LogUtils.printLog(logger, "{insert hist_possystem count{}-{}--{} }",insertHistPossystemCount,  splitedDate, insertHistPossystem);
	                                    }
	                                    
	                                    if (payFigBaseMap != null)
	                                    {
		                                    HashMap<String, BigDecimal> amtMap = new HashMap<String, BigDecimal>();
		                                    HashMap<String, BigDecimal> localAmtMap = new HashMap<String, BigDecimal>();
		                                    List<Map<String, Object>> orderPayList = jdbcTemplate.queryForList(SPLIT_DATE_SELECT_ORDERPAY_SQL_PATTERN, paramMap);
		                                    for (Map<String, Object> map : orderPayList)
		                                    {
		                                    	BigDecimal amt		= (BigDecimal) map.get("amt");
		                                    	BigDecimal localAmt	= (BigDecimal) map.get("local_amt");
		                                    	BigDecimal change	= (BigDecimal) map.get("change");
		                                    	String ccyNo	    = (String) map.get("cury_no");
		                                    	String payCode		= (String) map.get("pay_code");
		                                    	
		                                    	if (payCodeList.contains(payCode))
		                                    	{
			                                    	BigDecimal totalAmt			= (BigDecimal) amtMap.get(ccyNo);
			                                    	BigDecimal totalLocalAmt	= (BigDecimal) localAmtMap.get(ccyNo);
			                                    	if (totalAmt == null)
			                                    	{
			                                    		totalAmt = amt ;
			                                    		totalLocalAmt = localAmt ; 
			                                    	}
			                                    	else
			                                    	{
			                                    		totalAmt = totalAmt.add(amt) ;
			                                    		totalLocalAmt = totalLocalAmt.add(localAmt) ; 
			                                    	}
			                                    	amtMap.put(ccyNo, totalAmt);
			                                    	localAmtMap.put(ccyNo, totalLocalAmt);
		                                    	}
		                                    	
		                                    	BigDecimal totalAmt			= (BigDecimal) amtMap.get(baseCcyNo);
		                                    	BigDecimal totalLocalAmt	= (BigDecimal) localAmtMap.get(baseCcyNo);
		                                    	if (totalAmt == null)
		                                    	{
		                                    		totalAmt = change ;
		                                    		totalLocalAmt = change ; 
		                                    	}
		                                    	else
		                                    	{
		                                    		totalAmt = totalAmt.add(change) ;
		                                    		totalLocalAmt = totalLocalAmt.add(change) ; 
		                                    	}
		                                    	amtMap.put(baseCcyNo, totalAmt);
		                                    	localAmtMap.put(baseCcyNo, totalLocalAmt);
		                                    }
		                                    
		                                    for (String ccyNo : amtMap.keySet())
		                                    {
		                                    	BigDecimal amt = amtMap.get(ccyNo);
		                                    	BigDecimal localAmt = localAmtMap.get(ccyNo);
		                                    	
		                                    	payFigBaseMap.put("splitDate", splitedDate);
		                                    	payFigBaseMap.put("cury_no", ccyNo);
		                                    	payFigBaseMap.put("input_amt", amt);
		                                    	payFigBaseMap.put("local_amt", localAmt);
		                                    	
		                                    	int count = jdbcTemplate.update(SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN, payFigBaseMap);
		                                        LogUtils.printLog(logger, "SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN count{}-{}--{} }",count,  payFigBaseMap, SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN);
		
		//                                        if (splitedDate.before(businessDate))
		//                                        {
		//                                        	int count = jdbcTemplate.update(SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN, payFigBaseMap);
		//                                            LogUtils.printLog(logger, "SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN count{}-{}--{} }",count,  payFigBaseMap, SPLIT_DATE_INSERT_PAYFIG_SQL_PATTERN);
		//
		//                                        }
		//                                        else
		//                                        {
		//                                        	int count = jdbcTemplate.update(SPLIT_DATE_UPDATE_PAYFIG_SQL_PATTERN, payFigBaseMap);
		//                                            LogUtils.printLog(logger, "SPLIT_DATE_UPDATE_PAYFIG_SQL_PATTERN count{}-{}--{} }",count,  payFigBaseMap, SPLIT_DATE_UPDATE_PAYFIG_SQL_PATTERN);
		//                                        }
		                                    }
		                                    
		                                    jdbcTemplate.update(SPLIT_DATE_UPDATE_PAYFIG_A_SQL_PATTERN, paramMap);
		                                    jdbcTemplate.update(SPLIT_DATE_UPDATE_PAYFIG_E_SQL_PATTERN, paramMap);
		                                    jdbcTemplate.update(SPLIT_DATE_UPDATE_PAYFIG_S_SQL_PATTERN, paramMap);
	                                    }
	                                    
	                                    
	//                                    // payfig
	//                                    CallableStatement c = connection.prepareCall("{call split_payfig(?,?,?)}");
	//                                    c.setString(1, branchCode);
	//                                    c.setDate(2, businessDate);
	//                                    c.setDate(3, new java.sql.Date(splitedDate.getTime()));
	//                                    c.execute();
	//                                    c.close();
	//                                    LogUtils.printLog(logger, "split_payfig PROCEDURE call complete---{}",
	//                                    		splitedDate);
	                                }catch (Exception e){
	                                    LogUtils.printLog(logger,"split business date exception:{}",
	                                            e.getMessage());
	                                    e.printStackTrace();
	                                }
	
	                            }
                            }

                            lastControlDate = businessDate;
                        }
//                    }
//                    prcDates = new ArrayList<Date>(processDates);
                    readyDates = new ArrayList<Date>(processDates);
                        
		            if (waitStEodControls.size() > 0)
		            {
		                LogUtils.printLog(logger, "{} {} processPosDataToStg updatePollEodControl Wait For ST {} / {} ", branchCode, pollSchemeType, prcDates, readyDates);
		                updatePollEodControl(waitStEodControls, PollEodControl.STATUS_WAIT_ST);
		            }
		            if (readyEodControls.size() > 0)
		            {
		                LogUtils.printLog(logger, "{} {} processPosDataToStg updatePollEodControl Ready {} / {} ", branchCode, pollSchemeType, prcDates, readyDates);
		                updatePollEodControl(readyEodControls, PollEodControl.STATUS_STAGE);
		            }
		            stage = true ;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.printException(logger, "{" + branchCode + "} Branch processPosDataToStg Error ", e);
                taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
            } finally {
                updateTaskJobLog(taskLog, start && prcDates != null);
                if (!start) {
                    prcDates = null;
                }
//	        	if (TaskProcessStatus.FAILED.equals(taskLog.getStatus()))
//	        	{
//	                updateTaskJobLog(taskLog, prcDates != null);
//	            }
//	        	else if (!start || prcDates == null) {
//	
//	                LogUtils.printLog(logger, "{} Branch : processPosDataToStg {} delete ", branchScheme.getBranchMaster().getBranchCode(), taskLog.getId());
//	
////	                taskJobLogDao.deleteByKey(taskLog.getId());
//	                updateTaskJobLog(taskLog, false);
//
//	                prcDates = null;
//	            } else {
//	                updateTaskJobLog(taskLog, prcDates != null);
//	            }
            }
        }

        LogUtils.printLog(logger, "{} {} processPosDataToStg staging finish startEdw {}/{}", branchCode, pollSchemeType, prcDates, readyDates);

        try {

            if (prcDates != null) {
                //            for (Date date : prcDates) {
                //                LogUtils.printLog(logger, "{} Branch : prcDates {}", branchCode, date);
                //
                //            }

                if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD && readyDates.isEmpty()) {
                    LogUtils.printLog(logger, "{} {} processPosDataToStg No prcDates for EOD {}", branchCode, pollSchemeType, readyDates.size());
                } else {
                    BranchScheme branchSchemeToEdw = pollBranchSchemeService.getBranchScheme(branchScheme.getPollSchemeType(),
                            Direction.STG_TO_EDW, null, branchScheme.getBranchMaster().getBranchCode());
//                    BranchScheme branchSchemeToEdw = null;
                    if (branchSchemeToEdw != null) {

                        branchSchemeToEdw.setSchemeScheduleJob(branchScheme.getSchemeScheduleJob());
                        if (taskLog != null) {
                        	branchSchemeToEdw.setDependOnTaskLog(taskLog);
                        }

                        TaskJobLog taskLogEdw = taskJobLogService.acquireTaskJobLog(branchSchemeToEdw, schemeJobLog);
                        if (taskLogEdw == null) {
                            LogUtils.printLog(logger, "{} {}  processPosDataToStg EDW end (Exist Processing Task)", branchCode, pollSchemeType);
                            return null;
                        }

                        LogUtils.printLog(logger, "{} {} processPosDataToStg EDW task id {} / last task id {} ", branchCode, pollSchemeType,
                                taskLogEdw.getId(), taskLogEdw.getLastTaskJobLog() == null ? null : taskLogEdw.getLastTaskJobLog().getId());

                        if (taskLog != null) {
                            taskLogEdw.setDependOn(taskLog.getId());
                        }
                        taskLogEdw = taskJobLogService.startTaskJobLog(branchScheme, taskLogEdw);

                        try {
                            processStgToEdwJDBC(branchSchemeToEdw, controlDate, readyDates, taskLogEdw, logger);
                        } catch (Exception e) {
                            LogUtils.printException(logger, "processPosDataToStg processStgToEdwJDBC excpetion", e);
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        } finally {
                            updateTaskJobLog(taskLogEdw, true);
                        }
                    } else {
                        LogUtils.printLog(logger, "{} {} processPosDataToStg EDW No Oracle Configuration ", branchCode, pollSchemeType);
                    }
                    LogUtils.printLog(logger, "{} {} processPosDataToStg processStgToEdwJDBC Done", branchCode, pollSchemeType);
//		            if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
//		                LogUtils.printLog(logger, "{} {} processPosDataToStg insertPollEodControl {} ", branchCode, pollSchemeType, prcDates);
//		                updatePollEodControl(eodControls);
//		            }
                    done = true;
                }
            }
        } finally {
            if (branchScheme.getPollSchemeType() == PollSchemeType.SALES_EOD) {
                if (done) {
                    LogUtils.printLog(logger, "{} {} processPosDataToStg updatePollEodControl {} / {} ", branchCode, pollSchemeType, prcDates, readyDates);
                    updatePollEodControl(eodControls, PollEodControl.STATUS_COMPLETE);
                } else if (!stage) {
                    LogUtils.printLog(logger, "{} {} processPosDataToStg removePollEodControl {} / {} ", branchCode, pollSchemeType, prcDates, readyDates);
                    deletePollEodControlIfPending(eodControls);
                }
            }

        }
        return null;
    }

    protected abstract List<Date> doGetPosProcessDate(BranchScheme branchScheme, List<SchemeInfo> schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, Logger logger);

    protected abstract List<Date> doProcessPosDataToStg(BranchScheme branchScheme, List<SchemeInfo> schemeList, 
    		List<Date> procDates, List<Date> stDates, List<Date> nonStDoneDates, List<Date> stDoneDates, List<Date> stReady,
    		java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, TaskJobLog taskJobLog, Logger logger);

    protected abstract int getDefaultScanDayIfNoControl();

    protected abstract int getMaxScanDay();

    public String processStgToEdwJDBC(BranchScheme branchScheme, java.sql.Date controlDate, List<Date> prcDate, TaskJobLog taskLogEdw, Logger logger) {
        if (branchScheme != null && branchScheme.isEnabled()) {

            if (enableWebservice) {
                // Stg To EDW (WS)
                if (PollSchemeType.SALES_REALTIME.equals(branchScheme.getPollSchemeType())) {
                    try {
                        ResponseData responseData = realTimeService.processStgRealTimeDataToEdw(branchScheme.getBranchMaster().getBranchCode(), branchScheme.getBranchMaster().getMappingBranchCode(), logger);
                        if (responseData.isSuccess()) {
                            int count = Integer.parseInt(responseData.getData().toString());
                            LogUtils.printLog(logger, "Process data from Stg to EDW Success, insert {} data", responseData.getData());
                            taskJobLogService.createJobLogDetail(taskLogEdw, "webService", "webService", 0, count);
                        } else {
                            if (ResponseData.CODE.EXIST_EMPTY_DATA.getValue().equals(responseData.getCode())) {
                                LogUtils.printLog(logger, responseData.getMessage());
                            } else if (ResponseData.CODE.FAILURE.getValue().equals(responseData.getCode())) {
                                LogUtils.printLog(logger, "Process data from Stg to EDW transmission failure.");
                                LogUtils.printObject(logger, responseData);
                                taskJobLogService.createJobExceptionDetail(taskLogEdw, "webService", "webService", new Exception(responseData.getMessage()));
                            }
                        }
                    } catch (Exception e) {
                        taskJobLogService.createJobExceptionDetail(taskLogEdw, "webService", "webService", e);
                        LogUtils.printLog(logger, "Process data from Stg to EDW failure.", e);
                    }

                }
            }

            ProcessStgToEdwService processStgToEdwService = SpringBeanUtil.context.getBean(ProcessStgToEdwService.class);
            processStgToEdwService.setBranchScheme(branchScheme);
            processStgToEdwService.setTaskJobLog(taskLogEdw);
            processStgToEdwService.setDefaultTransactionBatchSize(defaultTransactionBatchSize);
            processStgToEdwService.setEnableBranchCodeMapping(enableBranchCodeMapping);            
            processStgToEdwService.setLogger(logger);
            LogUtils.printLog(logger, "{} {} processStgToEdwJDBC controlDate{} prcDates{}", branchScheme.getBranchMaster().getBranchCode(), branchScheme.getPollSchemeType(),
                    controlDate, prcDate);
            processStgToEdwService.processStgToEdwJDBC(controlDate, prcDate);
        } else {
            LogUtils.printLog(logger, "processStgToEdwJDBC invalid branchScheme");
        }
        return null;
    }

    /**
     * updateBussinessDate
     * <p>
     * update the Business Date according to the split date logic
     *
     * @param branchCode
     * @param toTable
     * @param cutOffTime
     * @param controlDate
     * @param logger
     * @return
     * @throws SQLException
     */
    private int updateBussinessDate(String branchCode, String toTable, String cutOffTime, java.sql.Date controlDate, java.sql.Date businessDate, Logger logger) throws SQLException {

        Calendar controlCal = Calendar.getInstance();
//		controlCal.add(Calendar.DATE, -1);
//		java.sql.Date yesterdayDate = new java.sql.Date(controlCal.getTime().getTime());


        controlCal.setTime(controlDate);
        controlCal.add(Calendar.DATE, 1);
        java.sql.Date controlNextDate = new java.sql.Date(controlCal.getTime().getTime());

        String splitDateSql = "update " + toTable + " set business_date = " +
                "case when convert(date, trans_datetime) > '" + businessDate + "' then '" + businessDate + "' " +
                "when convert(date, trans_datetime) <= '" + controlNextDate + "'  then '" + controlNextDate + "' " +
                "when convert(time, trans_datetime) < convert(time,'" + cutOffTime + "') then DATEADD(day ,-1 , convert(date,trans_datetime) ) " +
                "else convert(date, trans_datetime) end " +
                "where branch_code  = \'" + branchCode + "\' " +
//                              (dateStr == null ? "" : "and convert(date,business_date) in " + dateStr + " ") +
                "and convert(date,business_date) = '" + businessDate + "' " +
                "and business_date <> " +
                "case when convert(date, trans_datetime) > '" + businessDate + "' then '" + businessDate + "' " +
                "when convert(date, trans_datetime) <= '" + controlNextDate + "'  then '" + controlNextDate + "' " +
                "when convert(time, trans_datetime) < convert(time,'" + cutOffTime + "') then DATEADD(day ,-1 , convert(date,trans_datetime) ) " +
                "else convert(date, trans_datetime) end ";
//                              (dateStr == null ? "" : "and  business_date not in "+dateStr) ;
        LogUtils.printLog("{} SALES_EOD : {}{}{}splitSQL {}", branchCode, businessDate, controlDate, controlNextDate, splitDateSql);

//        try (Connection connection = applicationSettingService.getCurrentJDBCConnection()) {
//            return PosClientUtils.updateTable(connection, splitDateSql);
//        }
        return this.jdbcTemplate.update(splitDateSql, (Map<String, ?>) null);
    }

//    private List<Map<String, Object>> getSplitOrders(String branchCode, java.sql.Date controlDate, java.sql.Date businessDate, Logger logger) throws SQLException {
//
//        try (Connection connection = applicationSettingService.getCurrentJDBCConnection()) {
//
//            List<Date> dates = new ArrayList<Date>();
//            String sql = "select  branch_code, convert(varchar(10),business_date,120) business_date, order_no, recall from hist_orders where branch_code='" + branchCode + "' and business_date<='" + businessDate + "' and business_date > '" + controlDate + "' order by business_date";
//            LogUtils.printLog(logger, "{} Branch :split orders  {}", branchCode, sql);
//
//            return PosClientUtils.execCliectQuery(connection, sql, false);
//
//        }
//    }
//
    private List<Date> getBussinessDate(String branchCode, String toTable, java.sql.Date controlDate, java.sql.Date businessDate, Logger logger) throws SQLException {

//        try (Connection connection = applicationSettingService.getCurrentJDBCConnection()) {

//            List<Date> dates = new ArrayList<Date>();
            String sql = "select distinct business_date from " + toTable + 
            		" where branch_code='" + branchCode + 
            		"' and business_date > '" + controlDate + 
            		"' and business_date <= '" + businessDate + 
            		"' order by business_date";
            LogUtils.printLog("{} Branch :getBizDate {}", branchCode, sql);

//            List<Map<String, Object>> list = PosClientUtils.execCliectQuery(connection, sql, false);
//            for (Map<String, Object> map : list) {
//                dates.add((Date) map.get("business_date"));
//            }

            return this.jdbcTemplate.queryForList(sql, (Map<String, ?>) null, Date.class);
//        }
    }

    private List<PollEodControl> insertPendingPollEodControl(String branchCode, List<Date> dates, List<PollEodControl> controlList) {

        List<PollEodControl> eodControls = new ArrayList<PollEodControl>(dates.size());
        for (Date date : dates) {
        	
            PollEodControl pollEodControl = null ;
        	for (PollEodControl contorl :controlList )
        	{
            	java.sql.Date bzDate = new java.sql.Date(contorl.getBusinessDate().getTime()) ;
            	bzDate = java.sql.Date.valueOf(bzDate.toString());
        		if (bzDate.equals(date))
        		{
        			pollEodControl = contorl ;
        		}
        	}
        	if (pollEodControl != null)
        	{
        		eodControls.add(pollEodControl);
        		continue ;
        	}
        	
            pollEodControl = new PollEodControl();
//            pollEodControl.setCreateTime(new Date());
//            pollEodControl.setCreateUser("ESB_SYSTEM");
//            pollEodControl.setLastUpdateTime(new Date());
//            pollEodControl.setLastUpdateUser("ESB_SYSTEM");
            pollEodControl.setBranchCode(branchCode);
            pollEodControl.setBusinessDate(date);
            pollEodControl.setStatus(PollEodControl.STATUS_PENDING);
            Auditer.audit(pollEodControl);

            pollEodControl = pollEodControlDao.save(pollEodControl);

            eodControls.add(pollEodControl);
        }
        return eodControls;
    }


    private void updatePollEodControl(List<PollEodControl> eodControls, String status) {

        for (PollEodControl pollEodControl : eodControls) {
            pollEodControl.setStatus(status);
            Auditer.audit(pollEodControl);
            pollEodControlDao.save(pollEodControl);
        }
    }

    private void deletePollEodControlIfPending(List<PollEodControl> eodControls) {

    	if (eodControls != null)
    	{
	        for (PollEodControl pollEodControl : eodControls) {
	        	if (pollEodControl.getStatus().equals(PollEodControl.STATUS_PENDING))
	        	{
	        		pollEodControlDao.mergerDelete(pollEodControl);
	        	}
	        }
    	}
    }

//  private  List<Date>  doGetStockTakeDate(BranchScheme branchScheme, List schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate,Logger logger) {
//  return prcinigMasterService.getPosEODStockDateList(branchScheme,controlDate,logger);
//}

    protected List<Date> doGetStockTakeReadyDate(List<Date> stDates, BranchScheme branchScheme, List<SchemeInfo> schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate,Logger logger) {
//	  return prcinigMasterService.getPosEODStockDateList(branchScheme,controlDate,logger);

  	  List<Date> returnDates = new ArrayList<Date>();
	  List<Date> filterDates = new ArrayList<Date>();
      if (enableStockTakeTimeChecking)
      {
	      ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("CUT_OFF_TIME");
	      LocalTime cutOffTime = LocalTime.of(3, 0);
	      String cutOffTimeString = null;
	      if (applicationSetting != null) {
	          cutOffTimeString = applicationSetting.getCodeValue();
	      }
	      if (StringUtils.isNotBlank(cutOffTimeString)) {
	          String ss[] = cutOffTimeString.split(":");
	          if (ss.length >= 2) {
	              try {
	                  cutOffTime = LocalTime.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
	              } catch (Exception e) {
	                  LogUtils.printLog(logger, "CUT_OFF_TIME {} config invalid user default {}  .>>{}",
	                          cutOffTimeString,
	                          cutOffTime,
	                          e.getMessage());
	              }
	          }
	      }
	
		  for (Date date : stDates)
		  {
			   if (date.before(yesterdayDate))
			   {
				   returnDates.add(date);
			   }
			   else
			   {
				   if (date.equals(yesterdayDate))
				   {
				       if (LocalTime.now().isAfter(cutOffTime)) {
				           LogUtils.printLog(logger, "current time gt cutOffTime {}, stockTakeReady true ", cutOffTime);
						   returnDates.add(date);
				       }
				       else
				       {
				    	   filterDates.add(date);
				       }
				   }
				   else
				   {
			    	   filterDates.add(date);
				   }
			   }
		  }
      }
      else
      {
    	  filterDates = stDates ;
      }
      
	  if (!filterDates.isEmpty())
	  {
		  filterDates = doFilterStockTakeReady(filterDates, branchScheme, schemeList, currentDate, yesterdayDate, controlDate, logger);
		  returnDates.addAll(filterDates);
	  }
      return returnDates ;
  }
  protected abstract List<Date> doFilterStockTakeReady (List<Date> dates, BranchScheme branchScheme, List<SchemeInfo> schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate,Logger logger) ;	  
  
//   private boolean  stockTakeReady(BranchScheme branchScheme,java.sql.Date currentDate, Date date,Logger logger){
//	   
//	   if (date.after(currentDate))
//	   {
//	       ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("CUT_OFF_TIME");
//	       LocalTime cutOffTime = LocalTime.of(3, 0);
//	       String cutOffTimeString = null;
//	       if (applicationSetting != null) {
//	           cutOffTimeString = applicationSetting.getCodeValue();
//	       }
//	       if (StringUtils.isNotBlank(cutOffTimeString)) {
//	           String ss[] = cutOffTimeString.split(":");
//	           if (ss.length >= 2) {
//	               try {
//	                   cutOffTime = LocalTime.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
//	               } catch (Exception e) {
//	                   LogUtils.printLog(logger, "CUT_OFF_TIME {} config invalid user default {}  .>>{}",
//	                           cutOffTimeString,
//	                           cutOffTime,
//	                           e.getMessage());
//	               }
//	           }
//	       }
//	       if (LocalTime.now().isAfter(cutOffTime)) {
//	           LogUtils.printLog(logger, "current time gt cutOffTime {}, stockTakeReady true ", cutOffTime);
//	           return true;
//	       }
//	   }
//
//       return doStockTakeReady(branchScheme,currentDate, new java.sql.Date(date.getTime()),logger);
//   }
//   
//   protected abstract boolean doStockTakeReady(BranchScheme branchScheme,java.sql.Date currentDate, java.sql.Date date,Logger logger) ;
//
//    
//    private TaskJobLog createTaskJobLog(BranchScheme branchScheme) {
//        TaskJobLog taskJobLog = taskJobLogService.findLatestTaskJobLog(branchScheme);
//        if (taskJobLog != null) {
//            if (taskJobLog.getStatus() == TaskProcessStatus.PROGRESS) {
//            	if(System.currentTimeMillis() - taskJobLog.getLastUpdateTime().getTime() > 3600000){
//					LogUtils.printLog("{} branch code process continue 1 hour ,auto update status  to failed ",
//							branchScheme.getBranchMaster().getBranchCode());
//					taskJobLog.setStatus(TaskProcessStatus.FAILED);
//				} else {
//					return null;
//				}
//            }
//            taskJobLog.setLastestJobInd(LatestJobInd.N);
//            Auditer.audit(taskJobLog);
//            taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);
//        }
//
//        TaskJobLog taskLog = new TaskJobLog();
//        taskLog.setLastestJobInd(LatestJobInd.Y);
//        taskLog.setStatus(TaskProcessStatus.PROGRESS);
//        taskLog.setStartTime(new Date());
//        Auditer.audit(taskLog);
//        taskLog.setSchemeScheduleJob(branchScheme.getSchemeScheduleJob());
//        taskLog.setPollSchemeID(branchScheme.getId());
//        taskLog.setDirection(branchScheme.getDirection());
//        taskLog.setPollSchemeType(branchScheme.getPollSchemeType());

//    SchemeScheduleJob schemeScheduleJob = schemeScheduleJobService.getSchemeScheduleJob(branchScheme);
//    if(schemeScheduleJob!=null){
//        Long scheduleJobId = schemeScheduleJob.getId();
//        SchemeJobLog SchemeJobLog = schemeJobLogDao.findLatestSchemeJobLog(scheduleJobId);
//        taskLog.setSchemeJobLog(SchemeJobLog);
//    }

//        
//        taskLog.setBranchCode(branchScheme.getBranchMaster().getBranchCode());
//        taskLog.setPollBranchId(branchScheme.getBranchInfo().getId());
//        taskLog.setPollSchemeName(branchScheme.getPollSchemeName());
//        
//        taskLog = taskJobLogService.addOrUpdateTaskJobLog(taskLog);
//
//        return taskLog;
//    }


    private void updateTaskJobLog(TaskJobLog taskJobLog, boolean isComplete) {
        if (TaskProcessStatus.FAILED.equals(taskJobLog.getStatus())) {
            TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
            if (lastTaskJobLog != null) {
                if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
                    lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
                    taskJobLogDao.save(lastTaskJobLog);
                }
            }
            taskJobLog.setLastestJobInd(LatestJobInd.Y);
        } else {
            if (isComplete) {
                if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
                    taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
                }
            } else {
                TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
                boolean markFail = false;
                
                try
                {
                    if ( ( taskJobLog.getTaskJobExceptionDetails() != null && taskJobLog.getTaskJobExceptionDetails().size() > 0)
                    		|| ( taskJobLog.getTaskJobLogDetails() != null && taskJobLog.getTaskJobLogDetails().size() > 0) )
                    {
                        markFail = true;
                    }
                }
                catch (org.hibernate.LazyInitializationException e)
                {
                	
                }
                if (markFail)
                {
                    if (lastTaskJobLog != null) {
                        if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
                            lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
                            taskJobLogDao.save(lastTaskJobLog);
                        }
                    }
                    taskJobLog.setStatus(TaskProcessStatus.FAILED);
                    taskJobLog.setLastestJobInd(LatestJobInd.Y);
                }
                else
                {
	                taskJobLog.setStatus(TaskProcessStatus.NONE);
	                if (taskJobLog.getPollSchemeType() == PollSchemeType.SALES_REALTIME) {
	                    if (lastTaskJobLog != null) {
	                        if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
	                            lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
	                            taskJobLogDao.save(lastTaskJobLog);
	                        }
	                    }
	                    taskJobLog.setLastestJobInd(LatestJobInd.Y);
	                }
	                else
	                {
	                    if (lastTaskJobLog != null) {
	                        if (!LatestJobInd.Y.equals(lastTaskJobLog.getLastestJobInd())) {
	                            lastTaskJobLog.setLastestJobInd(LatestJobInd.Y);
	                            taskJobLogDao.save(lastTaskJobLog);
	                        }
	                    }
	                    taskJobLog.setLastestJobInd(LatestJobInd.N);
	                }
                }
            }
        }

//        if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
//            if (isComplete)
//                taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
//            else
//                taskJobLog.setStatus(TaskProcessStatus.NONE);
//        }
        taskJobLog.setEndTime(new Date());
        taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);

    }

}
