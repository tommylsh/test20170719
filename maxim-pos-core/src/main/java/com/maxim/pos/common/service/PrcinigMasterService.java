package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.BranchScheme;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by Lotic on 2017-05-17.
 */
public interface PrcinigMasterService {
    public List<Date> getPosEODStockDateList(BranchScheme branchScheme, java.sql.Date businessDate, Logger logger) ;
}
