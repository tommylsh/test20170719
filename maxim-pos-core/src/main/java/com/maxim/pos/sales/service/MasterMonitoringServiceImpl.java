package com.maxim.pos.sales.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PosClientUtils;

/**
 * Created by Joy on 2017/3/24.
 */
@Service("masterMonitoringService")
public class MasterMonitoringServiceImpl implements MasterMonitoringService{

    @Autowired
    private ApplicationSettingService applicationSettingService;

    @Override
    public boolean assertMonitoring(BranchScheme branchScheme){
        boolean bl = false;
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        try (Connection fromDS = applicationSettingService.getJDBCConection(branchScheme,true)){
            String query = "select * from MASTER_UPDATE_INFO where status = '' and branch_code = '"+ branchCode+"'";
            List<Map<String,Object>> list = PosClientUtils.execCliectQuery(fromDS,query,true);
            if(list.size()>0 && !list.isEmpty()) {
                bl = true;
            }
        } catch(Exception e) {
        	LogUtils.printException("get connection is null",e);
        }

        return bl;
    }

    @Override
    public boolean updateStatus(BranchScheme branchScheme) {
        boolean bl = false;
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        try (Connection fromDS = applicationSettingService.getJDBCConection(branchScheme,true)){
            String query = "update MASTER_UPDATE_INFO set status = 'C' where status = '' and branch_code = '"+ branchCode+"'";
            bl = PosClientUtils.updateTable(fromDS,query) > 0 ? true : false ;
        } catch(Exception e) {
        	LogUtils.printException("get connection is null or sql execute fail",e);
        }

        return bl;
    }
}
