package com.maxim.pos.sales.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxim.enums.RequestMethod;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.SalesRealTimeTable;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.sales.persistence.RealTimeDao;
import com.maxim.rest.ResponseData;
import com.maxim.util.HttpUtils;
import com.maxim.util.JsonUtils;

@Service(RealTimeService.BEAN_NAME)
public class RealTimeServiceImpl implements RealTimeService {

    @Autowired
    private RealTimeDao realTimeDao;

    @Autowired
    private ApplicationSettingService applicationSettingService;

    @Autowired
    private PollSchemeInfoService pollSchemeInfoService;

    @Override
    public ResponseData processStgRealTimeDataToEdw(String branchCode, String mappingBranchCode, Logger logger) {
        return processStgRealTimeDataToEdw(branchCode, mappingBranchCode, null, logger);
    }

    @Override
    public ResponseData processStgRealTimeDataToEdw(String branchCode, String mappingBranchCode, Date businessDate, Logger logger) {
        if (branchCode == null || StringUtils.isBlank(branchCode)) {
            throw new IllegalArgumentException("[Validation failed] - this argument [branchCode] is required; it must not be null");
        }
        // if (businessDate == null) {
        //    throw new IllegalArgumentException("[Validation failed] - this argument [businessDate] is required; it must not be null");
        // }

        SchemeInfo ordersSchemeInfo = getSchemeInfo(SalesRealTimeTable.ORDERS, logger);
        List<Map<String, Object>> ordersList = realTimeDao.getRealTimeDataList(ordersSchemeInfo, branchCode, mappingBranchCode, businessDate);

        SchemeInfo ordersPaySchemeInfo = getSchemeInfo(SalesRealTimeTable.ORDERS_PAY, logger);
        List<Map<String, Object>> ordersPayList = realTimeDao.getRealTimeDataList(ordersPaySchemeInfo, branchCode, mappingBranchCode, businessDate);

        SchemeInfo ordersExtraSchemeInfo = getSchemeInfo(SalesRealTimeTable.ORDERS_EXTRA, logger);
        List<Map<String, Object>> ordersExtraList = realTimeDao.getRealTimeDataList(ordersExtraSchemeInfo, branchCode, mappingBranchCode, businessDate);

//        if (ordersPayList.isEmpty() || ordersList.isEmpty() || ordersExtraList.isEmpty()) {
          if (ordersPayList.isEmpty() || ordersList.isEmpty()) {
            return new ResponseData(ResponseData.CODE.EXIST_EMPTY_DATA.getValue(), "ordersPayList & ordersList & ordersExtraList must not be empty. " +
                    "But found ordersPayList.isEmpty():" + ordersPayList.isEmpty()
                    + ", ordersList.isEmpty():" + ordersList.isEmpty()
                    + ", ordersExtraList.isEmpty():" + ordersExtraList.isEmpty(), null, Boolean.FALSE);
        }

        SchemeInfo couponSalesSchemeInfo = getSchemeInfo(SalesRealTimeTable.COUPON_SALES, logger);
        List<Map<String, Object>> couponSalesList = realTimeDao.getRealTimeDataList(couponSalesSchemeInfo, branchCode, mappingBranchCode, businessDate);

        ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("REAL_TIME_RS");
        if (applicationSetting == null || StringUtils.isBlank(applicationSetting.getCodeValue())) {
            // LogUtils.printLog(logger, "Can't get the value of 'REAL_TIME_RS' from application settings, please contact the administrator.");
            return new ResponseData(ResponseData.CODE.APP_CONFIG_ERROR.getValue(), "Can't get the value of 'REAL_TIME_RS' from application settings", null, Boolean.FALSE);
        }

        ResponseData responseData = callWebService(ordersList, ordersPayList, ordersExtraList, couponSalesList);
        if (responseData.isSuccess()) {
            if (!couponSalesList.isEmpty()) {
                realTimeDao.updateStatus(couponSalesSchemeInfo.getSource(), branchCode, businessDate);
            }
            realTimeDao.updateStatus(ordersSchemeInfo.getSource(), branchCode, businessDate);
            realTimeDao.updateStatus(ordersPaySchemeInfo.getSource(), branchCode, businessDate);
            realTimeDao.updateStatus(ordersExtraSchemeInfo.getSource(), branchCode, businessDate);
        }
        return responseData;
    }

    private ResponseData callWebService(List<Map<String, Object>> ordersList,
                                        List<Map<String, Object>> ordersPayList,
                                        List<Map<String, Object>> ordersExtraList,
                                        List<Map<String, Object>> couponSalesList) {

        ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("REAL_TIME_RS");
        if (applicationSetting == null || StringUtils.isBlank(applicationSetting.getCodeValue())) {
            return new ResponseData(ResponseData.CODE.APP_CONFIG_ERROR.getValue(), "Can't get the value of 'REAL_TIME_RS' from application settings", null, Boolean.FALSE);
        }

        try {
//            String jsonParam = JsonUtils.toJson(new RealTimeSalesData(ordersList, ordersPayList, ordersExtraList, couponSalesList));
           Object obj = new RealTimeSalesData(ordersList, ordersPayList, ordersExtraList, couponSalesList);
            String jsonResult = HttpUtils.jasonRequest(RequestMethod.POST, applicationSetting.getCodeValue(), obj);
            return JsonUtils.fromJson(jsonResult, ResponseData.class);
        } catch (Exception e) {
            return new ResponseData(ResponseData.CODE.FAILURE.getValue(), e.getMessage(), null, Boolean.FALSE);
        }

    }

    private SchemeInfo getSchemeInfo(SalesRealTimeTable salesRealTimeTable, Logger logger) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pollSchemeType", PollSchemeType.SALES_REALTIME);
        paramMap.put("clientType", ClientType.WEBSERVICE);
        paramMap.put("destination", salesRealTimeTable.getTableName());

        List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfo(paramMap);
        if (schemeInfoList.size() < 1) {
            LogUtils.printLog(logger, "Can't get the records which destination={}", salesRealTimeTable.getTableName());
            throw new RuntimeException("Can't get the records which destination=" + salesRealTimeTable.getTableName());
        }
        if (schemeInfoList.size() > 1) {
            LogUtils.printLog(logger, "Get multi records which destination={} and clientType={}, please check the configuration of table 'POLL_SCHEME_INFO' is correct.", salesRealTimeTable.getTableName(), ClientType.ORACLE);
            throw new RuntimeException("Get multi records which destination="
                    + salesRealTimeTable.getTableName() + " and clientType=" + ClientType.ORACLE
                    + ", please check the configuration of table 'POLL_SCHEME_INFO' is correct.");
        }
        return schemeInfoList.get(0);
    }

    static class RealTimeSalesData {

        private final List<Map<String, Object>> ordersList;
        private final List<Map<String, Object>> ordersPayList;
        private final List<Map<String, Object>> ordersExtraList;
        private final List<Map<String, Object>> couponSalesList;

        public RealTimeSalesData(List<Map<String, Object>> ordersList,
                                 List<Map<String, Object>> ordersPayList,
                                 List<Map<String, Object>> ordersExtraList,
                                 List<Map<String, Object>> couponSalesList) {
            this.ordersList = ordersList;
            this.ordersPayList = ordersPayList;
            this.ordersExtraList = ordersExtraList;
            this.couponSalesList = couponSalesList;
        }

        public List<Map<String, Object>> getOrdersList() {
            return ordersList;
        }

        public List<Map<String, Object>> getOrdersPayList() {
            return ordersPayList;
        }

        public List<Map<String, Object>> getOrdersExtraList() {
            return ordersExtraList;
        }

        public List<Map<String, Object>> getCouponSalesList() {
            return couponSalesList;
        }
    }

}
