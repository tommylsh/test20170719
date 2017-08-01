package com.maxim.pos.test.sales.service;

import com.maxim.pos.common.data.MasterSyncType;
import com.maxim.pos.sales.service.MasterSyncToStgService;
import com.maxim.pos.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MasterSyncToStgServiceTest extends BaseTest {

    @Autowired
    private MasterSyncToStgService masterSyncToStgService;

    @Test
//    @Transactional
    public void testProcessMasterDataToStg() {
        MasterSyncType masterSyncType = new MasterSyncType();
        masterSyncType.setType(MasterSyncType.Type.BRANCH_CODE.name());
        masterSyncType.setValue("5014");
        masterSyncToStgService.processMasterDataToStg(masterSyncType);
    }

}
