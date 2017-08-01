package com.maxim.pos.test.sales.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.sales.service.FileCopyService;
import com.maxim.pos.test.common.BaseTest;

public class FileCopyTest extends BaseTest {

    @Autowired
    private FileCopyService fileCopyService;

    @Test
//    @Transactional
    public void test() {
    	BranchScheme branchScheme = new BranchScheme();
    	branchScheme.setPollSchemeType(PollSchemeType.OCT_TO_POS);
    	branchScheme.setDirection(Direction.OCT_ALL);
    	fileCopyService.fileCopy(branchScheme, logger);
    }

}
