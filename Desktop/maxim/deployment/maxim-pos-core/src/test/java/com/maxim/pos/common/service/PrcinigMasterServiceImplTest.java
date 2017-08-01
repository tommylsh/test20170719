package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.test.common.BaseTest;
import javafx.geometry.Pos;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Lotic on 2017-05-17.
 */
public class PrcinigMasterServiceImplTest extends BaseTest {
    @Autowired
    private PrcinigMasterService prcinigMasterService;
    @Autowired
    private PosSystemService posSystemService;
    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;
    @Test
    public  void testGetPosEODStockDateList(){
//        BranchScheme branchScheme = new BranchScheme();
//        BranchMaster branchMaster = new BranchMaster();
//        branchMaster.setBranchCode("1284");
//        branchScheme.setBranchMaster(branchMaster);

        BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_EOD, Direction.POS_TO_STG, ClientType.SQLPOS,"1284");
        List<java.util.Date> list =  prcinigMasterService.getPosEODStockDateList(branchScheme,new Date(new java.util.Date().getTime()),null);
        for (java.util.Date date:
                list) {
            System.out.println("****"+date);
        }

//      boolean stockTakeReady =   posSystemService.stockTakeReady(branchScheme,new Date(new java.util.Date().getTime()),null);
//        System.out.println("&&&&&&&&&"+stockTakeReady);
    }

}