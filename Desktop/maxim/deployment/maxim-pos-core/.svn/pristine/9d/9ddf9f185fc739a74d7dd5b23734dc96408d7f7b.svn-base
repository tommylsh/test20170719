package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.value.CommonCriteria;

import java.util.List;

public interface BranchMasterService {

    List<BranchMaster> getBranchMasterList();

    List<BranchMaster> getBranchMasterList(String branchType);

    BranchMaster getBranchMaster(String branchCode);

    void save(BranchMaster branchMaster);

    void delete(Long branchMasterId);

    List<BranchMaster> findBranchMasterByCriteria(CommonCriteria criteria);

    Long getBranchMasterCountByCriteria(CommonCriteria criteria);

}
