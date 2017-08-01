package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.persistence.BranchMasterDao;
import com.maxim.pos.common.value.CommonCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Transactional
@Service("branchMasterService")
public class BranchMasterServiceImpl implements BranchMasterService {

    @Autowired
    private BranchMasterDao branchMasterDao;
//    @PostConstruct
//    public void init(){
//        List<BranchMaster> lists  = getBranchMasterList();
//
//        for (BranchMaster bm:
//                lists) {
//            if(StringUtils.isNotBlank(bm.getMappingBranchCode())){
//                ContextUtils.MAPPING_BRANCH_CODE.put(bm.getBranchCode(),bm.getMappingBranchCode());
//            }
//        }
//    }

    @Override
    public List<BranchMaster> getBranchMasterList() {
        return branchMasterDao.getBranchMasterList();
    }

    @Override
    public List<BranchMaster> getBranchMasterList(String branchType) {
        Assert.hasText(branchType);
        return branchMasterDao.getBranchMasterList(branchType);
    }

    @Override
    public BranchMaster getBranchMaster(String branchCode) {
        Assert.hasText(branchCode);
        return branchMasterDao.getBranchMaster(branchCode);
    }

    @Override
    public void save(BranchMaster branchMaster) {
        if (branchMaster == null) {
            throw new RuntimeException("[Validation failed] - this argument [branchMaster] is required; it must not be null");
        }
        branchMasterDao.save(branchMaster);
    }

    @Override
    public void delete(Long branchMasterId) {
        if (branchMasterId == null) {
            throw new RuntimeException("[Validation failed] - this argument [branchMasterId] is required; it must not be null");
        }
        BranchMaster branchMaster = branchMasterDao.getById(branchMasterId);
        if (branchMaster == null) {
            throw new RuntimeException("The record which branchMasterId=" + branchMasterId + " is not exist.");
        }
        branchMasterDao.delete(branchMaster);
    }

    @Override
    public List<BranchMaster> findBranchMasterByCriteria(CommonCriteria criteria) {
        return branchMasterDao.findBranchMasterByCriteria(criteria);
    }

    @Override
    public Long getBranchMasterCountByCriteria(CommonCriteria criteria) {
        return branchMasterDao.getBranchMasterCountByCriteria(criteria);
    }

}
