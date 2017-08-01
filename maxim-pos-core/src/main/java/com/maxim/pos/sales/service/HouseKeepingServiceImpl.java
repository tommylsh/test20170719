package com.maxim.pos.sales.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.sales.persistence.HouseKeepingDao;

@Service("houseKeepingService")
@Transactional
public class HouseKeepingServiceImpl implements HouseKeepingService {

    @Autowired
    private ApplicationSettingService applicationSettingService;

    @Autowired
    private HouseKeepingDao houseKeepingDao;

    @Override
    public void houseKeeping(Logger logger) {
        try {
            String value = applicationSettingService.getApplicationSettingCodeValue("HOUSE_KEEPING");
            LogUtils.printLog(logger, "start housekeeping  {} days before", value);
            if (StringUtils.isNotBlank(value)) {
                int days = Integer.parseInt(value);
                LogUtils.printLog(logger, "housekeeping  task log detail count {} completed,", houseKeepingDao.removeDetailList(days));
                LogUtils.printLog(logger, "housekeeping  task log exception detail count {} completed,", houseKeepingDao.removeExceptionList(days));
                LogUtils.printLog(logger, "housekeeping  task log detail count {} completed,", houseKeepingDao.removeTaskJobLog(days));
                LogUtils.printLog(logger, "housekeeping  scheme job count {} completed,", houseKeepingDao.removeSchemeJobLog(days));
            } else {
                LogUtils.printLog(logger, "HOUSE_KEEPING code not found");
            }
        } catch (Exception e) {
            LogUtils.printException(logger, "houseKeeping exception", e);
        }
    }
}