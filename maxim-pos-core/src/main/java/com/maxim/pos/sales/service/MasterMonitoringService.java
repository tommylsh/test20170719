package com.maxim.pos.sales.service;

import com.maxim.pos.common.entity.BranchScheme;

/**
 * Created by Joy on 2017/3/24.
 */
public interface MasterMonitoringService {

    public boolean assertMonitoring(BranchScheme branchScheme);

    public boolean updateStatus(BranchScheme branchScheme);
}
