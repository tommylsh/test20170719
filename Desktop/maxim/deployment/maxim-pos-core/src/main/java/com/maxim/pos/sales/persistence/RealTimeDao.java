package com.maxim.pos.sales.persistence;

import com.maxim.pos.common.entity.SchemeInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RealTimeDao {

    List<Map<String, Object>> getRealTimeDataList(SchemeInfo schemeInfo, String branchCode, String mapBranchCode, Date businessDate);

    int updateStatus(String sourceTable, String branchCode, Date businessDate);

}
