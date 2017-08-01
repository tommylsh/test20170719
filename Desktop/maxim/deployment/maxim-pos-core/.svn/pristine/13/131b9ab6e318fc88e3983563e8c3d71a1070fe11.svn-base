package com.maxim.pos.sales.service;

import com.maxim.rest.ResponseData;
import org.slf4j.Logger;

import java.util.Date;

public interface RealTimeService {

    String BEAN_NAME = "realTimeService";

    ResponseData processStgRealTimeDataToEdw(String branchCode, String mappingBranchCode, Logger logger);

    ResponseData processStgRealTimeDataToEdw(String branchCode, String mappingBranchCode, Date businessDate, Logger logger);

}
