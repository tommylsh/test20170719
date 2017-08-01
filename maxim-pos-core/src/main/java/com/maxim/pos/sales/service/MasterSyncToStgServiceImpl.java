package com.maxim.pos.sales.service;

import com.maxim.exception.ValidateException;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.data.MasterSyncType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("masterSyncToStgService")
public class MasterSyncToStgServiceImpl implements MasterSyncToStgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterSyncToStgService.class);

    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    @Autowired
    private BranchMasterService branchMasterService;

    @Autowired
    private MasterService masterService;

    @Override
    public void processMasterDataToStg(MasterSyncType masterSyncType) {
        if (masterSyncType == null) {
            throw new ValidateException("this argument [masterSyncType] is required; it must not be null");
        }
        MasterSyncType.Type type = MasterSyncType.fromType(masterSyncType.getType());
        if (type == null) {
            throw new ValidateException("this argument [type] must match {BRANCH_CODE, BRANCH_TYPE, ALL}; it must not be null");
        }
        if (type == MasterSyncType.Type.BRANCH_CODE) {
            String branchCode = masterSyncType.getValue();
            if (StringUtils.isBlank(branchCode)) {
                throw new ValidateException("this argument [value] is required; it must not be null");
            }
            BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.MST_TO_STG, ClientType.SQLSERVER, branchCode);
            if (branchScheme == null) {
                throw new ValidateException("Can't get the record from DB which branchCode=" + branchCode);
            }
            masterService.processMasterServerToStaging(branchScheme, null, LOGGER);
            return;
        }

        if (type == MasterSyncType.Type.BRANCH_TYPE) {
            String branchType = masterSyncType.getValue();
            if (StringUtils.isBlank(branchType)) {
                throw new ValidateException("this argument [value] is required; it must not be null");
            }
            List<BranchMaster> branchMasterList = branchMasterService.getBranchMasterList(branchType);
            for (BranchMaster branchMaster : branchMasterList) {
                BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.MST_TO_STG, ClientType.SQLSERVER, branchMaster.getBranchCode());
                masterService.processMasterServerToStaging(branchScheme, null, LOGGER);
            }
            return;
        }

        List<BranchMaster> branchMasterList = branchMasterService.getBranchMasterList();
        for (BranchMaster branchMaster : branchMasterList) {
            BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.MST_TO_STG, ClientType.SQLSERVER, branchMaster.getBranchCode());
            masterService.processMasterServerToStaging(branchScheme, null, LOGGER);
        }
    }

}
