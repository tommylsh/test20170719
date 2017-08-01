package com.maxim.pos.sales.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.sales.persistence.BranchInfoDao;

@Transactional
@Service(BranchInfoService.BEAN_NAME)
public class BranchInfoServiceImpl implements BranchInfoService {

    @Autowired
    private BranchInfoDao branchInfoDao;

    @Override
    public void save(BranchInfo branchInfo) {
        if (branchInfo == null) {
            throw new RuntimeException("[Validation failed] - this argument [branchInfo] is required; it must not be null");
        }
        branchInfoDao.save(branchInfo);
    }

    @Override
    public void delete(Long branchInfoId) {
        if (branchInfoId == null) {
            throw new RuntimeException("[Validation failed] - this argument [branchInfoId] is required; it must not be null");
        }
        BranchInfo scheduleJob = branchInfoDao.getById(branchInfoId);
        if (scheduleJob == null) {
            throw new RuntimeException("The record which branchInfoId=" + branchInfoId + " is not exist.");
        }
        branchInfoDao.delete(scheduleJob);
    }

    @Override
    public List<BranchInfo> findBranchInfoList(Map<String, Object> paramMap) {
        return branchInfoDao.findBranchInfoList(paramMap);
    }

    @Override
    public List<BranchInfo> findBranchInfoByCriteria(CommonCriteria criteria) {
        return branchInfoDao.findBranchInfoByCriteria(criteria);
    }

    @Override
    public Long getBranchInfoCountByCriteria(CommonCriteria criteria) {
        return branchInfoDao.getBranchInfoCountByCriteria(criteria);
    }

}