package com.maxim.pos.common.service;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.report.service.ReportService;
import com.maxim.pos.sales.persistence.SchemeInfoDao;
import com.maxim.pos.sales.service.FileCopyService;
import com.maxim.pos.sales.service.HouseKeepingService;
import com.maxim.pos.sales.service.MasterService;
import com.maxim.pos.sales.service.SalesService;
import com.maxim.pos.sales.service.SmtpService;

public class BranchSchemeExecutor implements Runnable, Callable<String> {
	public static final Logger LOGGER = LoggerFactory.getLogger(BranchSchemeExecutor.class);
	private BranchScheme branchScheme;
	private SchemeJobLog schemeJobLog;
	private Logger logger;
//	private PosSystemService posSystemService;
//	private PollEodControlDao pollEodControlDao;
//	private SalesServiceOld salesService;
	
	private static SchemeInfoDao schemeInfoDao;
	
	private static ReportService reportService;
	private static MasterService masterService;
	private static SmtpService smtpService;
	private static FileCopyService fileCopyService;
//	private static MasterMonitoringService masterMonitoringService;
	private static HouseKeepingService houseKeepingService;
	private static boolean init = false ;
	
	private static HashMap<ClientType, SalesService> salesServiceMap ;

	public BranchSchemeExecutor(){
		if (!init)
		{
//			salesService  = (SalesServiceOld)SpringBeanUtil.context.getBean("salesService");
			
			schemeInfoDao = (SchemeInfoDao)SpringBeanUtil.context.getBean("schemeInfoDao");
			masterService = (MasterService)SpringBeanUtil.context.getBean("masterService");
	//		posSystemService = (PosSystemService)SpringBeanUtil.context.getBean("posSystemService");
//			masterMonitoringService = (MasterMonitoringService) SpringBeanUtil.context.getBean("masterMonitoringService");
	//		pollEodControlDao = (PollEodControlDao) SpringBeanUtil.context.getBean("pollEodControlDao");
			smtpService = (SmtpService) SpringBeanUtil.context.getBean("smtpService");
			fileCopyService = (FileCopyService) SpringBeanUtil.context.getBean("fileCopyService");
			houseKeepingService = (HouseKeepingService) SpringBeanUtil.context.getBean("houseKeepingService");
			reportService = (ReportService)SpringBeanUtil.context.getBean("reportService");

			salesServiceMap = new HashMap<ClientType, SalesService>();

//			ClientType[] clientTypes = ClientType.values();
//			for (ClientType ct:
//					clientTypes ) {
//				if(StringUtils.startsWith(ct.name(),"SQLPOS")){
//					salesServiceMap.put(ct, (SalesService)SpringBeanUtil.context.getBean("sqlSalesService"));
//				}
//			}
//
			salesServiceMap.put(ClientType.SQLSERVER, (SalesService)SpringBeanUtil.context.getBean("sqlSalesService"));
			salesServiceMap.put(ClientType.SQLPOS, (SalesService)SpringBeanUtil.context.getBean("sqlSalesService"));
			salesServiceMap.put(ClientType.ORACLE, (SalesService)SpringBeanUtil.context.getBean("sqlSalesService"));
			salesServiceMap.put(ClientType.DBF, (SalesService)SpringBeanUtil.context.getBean("dbfSalesService"));
			salesServiceMap.put(ClientType.CSV, (SalesService)SpringBeanUtil.context.getBean("csvSalesService"));
			init = true ;
		}
	}

	public String call() {
		// TODO Auto-generated method stub
		if (logger == null) {
			logger = LOGGER;
		}
		LogUtils.setCurrentThreadLogger(logger);
//        LogUtils.printLog(logger, "BranchSchemeExecutor Start {}", branchScheme.toString());

		if (branchScheme != null) {
			// process branchScheme
			switch (branchScheme.getPollSchemeType()) {

			case SALES_REALTIME:
			case SALES_EOD:
				ClientType clientType = branchScheme.getBranchInfo().getClientType() ;
				SalesService salesService = salesServiceMap.get(clientType);
				if (salesService == null)
				{
					synchronized (salesServiceMap)
					{
						salesService = salesServiceMap.get(ClientType.SQLPOS);
						salesServiceMap.put(clientType, salesService);
					}
				}
				
				if (salesService != null)
				{
					salesService.processPosDataToStg(branchScheme, schemeJobLog, logger);
				}
				else
				{
					logger.error("No Sales Handler for {}" + branchScheme.getBranchInfo().getClientType());
				}
				break;
			case MASTER:
				switch (branchScheme.getDirection()) {
				case MST_TO_STG:
					masterService.processMasterServerToStaging(branchScheme, schemeJobLog, logger);
//					if(branchScheme.isReRun() || masterMonitoringService.assertMonitoring(branchScheme)){
//						boolean result = masterService.processMasterServerToStaging(branchScheme, schemeJobLog, logger);
//						if (result) {
//							masterMonitoringService.updateStatus(branchScheme);	
//						}
//					} else {
//						logger.error("current BranchScheme:" + branchScheme +" don't have trigger event");
//					}
					break;
				case STG_TO_POS:
					masterService.processStagingToPos(branchScheme, logger);
					break;

				default:

					break;
				}
				break;
			case MASTER_COPY:
				masterService.processFolderCopy(branchScheme, schemeJobLog, logger);
				break;
			case SMTP:
				smtpService.sendMail(logger);
				break;
			case REPORT:
				reportService.sendReportMail(branchScheme, logger);
				break;
			case OCT_TO_POS:
				if ("TARGET".equals(branchScheme.getPollSchemeName()))
				{
					fileCopyService.fileCopyOneTarget(branchScheme, logger);
				}
				else
				{
					fileCopyService.fileCopy(branchScheme,logger);
				}
//				if (branchScheme.getDirection().equals(Direction.FILE)) {
////					masterService.processFolderCopy(branchScheme, schemeJobLog, logger);
//					fileCopyService.fileCopy(branchScheme,logger);
//				}
				break;
			case HOUSE_KEEPING:
				houseKeepingService.houseKeeping(logger);
				break;
			default:

				break;
			}

		} else {
			LogUtils.printLog(logger, " BranchScheme is null");
		}
		
//        LogUtils.printLog(logger, "BranchSchemeExecutor End {}", branchScheme.toString());

		return null;
	}

	public BranchScheme getBranchScheme() {
		return branchScheme;
	}

	public void setBranchScheme(BranchScheme branchScheme) {
		this.branchScheme = branchScheme;
	}

	public SchemeJobLog getSchemeJobLog() {
		return schemeJobLog;
	}

	public void setSchemeJobLog(SchemeJobLog schemeJobLog) {
		this.schemeJobLog = schemeJobLog;
	}


	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void run() {
		try
		{
			if (branchScheme.getId() != null)
			{
	            BranchScheme thisBranchScheme = schemeInfoDao.getSingle(BranchScheme.class, branchScheme.getId());
	            thisBranchScheme.setSchemeScheduleJob(branchScheme.getSchemeScheduleJob());
	            thisBranchScheme.setTaskLog(branchScheme.getTaskLog());
	            thisBranchScheme.setSchemeJobLog(schemeJobLog);
	            thisBranchScheme.setReRun(branchScheme.isReRun());
	            thisBranchScheme.setBusinessDate(branchScheme.getBusinessDate());
	            this.branchScheme = thisBranchScheme ;
			}
			call();
		}
		catch (Exception e)
		{
	        LogUtils.printException(logger, "BranchSchemeExecutor Error {}", e);
		}
	}

}
