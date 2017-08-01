package com.maxim.pos.sales.service;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.value.CommonCriteria;

import java.util.List;
import java.util.Map;

public interface BranchInfoService {

    String BEAN_NAME = "branchInfoService";

    void save(BranchInfo branchInfo);

    void delete(Long branchInfoId);

    List<BranchInfo> findBranchInfoList(Map<String,Object> paramMap);

    List<BranchInfo> findBranchInfoByCriteria(CommonCriteria criteria);

    Long getBranchInfoCountByCriteria(CommonCriteria criteria);

}
