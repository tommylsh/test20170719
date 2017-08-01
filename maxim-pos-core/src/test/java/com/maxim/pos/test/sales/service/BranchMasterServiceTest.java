package com.maxim.pos.test.sales.service;

import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

public class BranchMasterServiceTest extends BaseTest {

    @Autowired
    private BranchMasterService branchMasterService;

    @Test
    @Transactional(readOnly = true)
    public void testGetBranchMaster() {
        BranchMaster branchMaster = branchMasterService.getBranchMaster("5014");
        Assert.notNull(branchMaster);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetBranchMasterList() {
        List<BranchMaster> branchMasterList = branchMasterService.getBranchMasterList();
        Assert.notEmpty(branchMasterList);
    }

}
