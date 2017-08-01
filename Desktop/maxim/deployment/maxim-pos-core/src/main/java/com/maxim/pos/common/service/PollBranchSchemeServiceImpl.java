package com.maxim.pos.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.SystemDashboard;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.common.value.SystemDashboardQueryCriteria;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.pos.sales.persistence.BranchSchemeDao;
import com.maxim.pos.sales.persistence.SchemeInfoDao;

@Service("pollBranchSchemeService")
@Transactional
public class PollBranchSchemeServiceImpl implements PollBranchSchemeService {
    
//    private static final String SELECT_STATEMENT = "SELECT {0} FROM {1} {2}";
//    private static final String INSERT_STATEMENT = "INSERT INTO {0} ({1}) VALUES ({2})";
//    private static final String UPDATE_STATEMENT = "UPDATE {0} SET {1} WHERE {2}";
    
    @Autowired
    private SchemeInfoDao schemeInfoDao;

    @Autowired
    private BranchSchemeDao branchSchemeDao;

    @Override
    public BranchScheme savePollBranchScheme(BranchScheme schema) {
        return (BranchScheme) schemeInfoDao.save(schema);
    }

	@Override
	public List<BranchScheme> getBranchSchemeByScheduleJob(SchemeScheduleJob schemeScheduleJob) {
		return schemeInfoDao.findByPollSchemeType(schemeScheduleJob.getPollSchemeType(),schemeScheduleJob.getPollSchemeDirection());
	}

	@Override
	public BranchScheme getBranchScheme(PollSchemeType pollSchemeType, Direction direction,ClientType clientType,String branchCode) {
		return schemeInfoDao.findbyPollSchemeTypeAndDirectionAndClientType(pollSchemeType,direction,clientType,branchCode);
	}

    @Override
    public List<BranchScheme> findBranchSchemeByCriteria(CommonCriteria criteria) {
        return branchSchemeDao.findBranchSchemeByCriteria(criteria);
    }

    @Override
    public Long getBranchSchemeCountByCriteria(CommonCriteria criteria) {
        return branchSchemeDao.getBranchSchemeCountByCriteria(criteria);
    }

    @Override
    public void delete(Long branchSchemeId) {
        if (branchSchemeId == null) {
            throw new RuntimeException("[Validation failed] - this argument [branchSchemeId] is required; it must not be null");
        }
        BranchScheme branchScheme = branchSchemeDao.getById(branchSchemeId);
        if (branchScheme == null) {
            throw new RuntimeException("The record which branchSchemeId=" + branchSchemeId + " is not exist.");
        }
        branchSchemeDao.delete(branchScheme);
    }
    
    public List<TaskJobLog> findEodProcess(TaskJobLogQueryCriteria criteria) {
		return branchSchemeDao.findEodProcess(criteria);
	}

	public Long getEodProcessCountByCriteria(TaskJobLogQueryCriteria criteria) {
		return branchSchemeDao.getEodProcessCountByCriteria(criteria);
	}

	@Override
	public List<SystemDashboard> findSystemDashboard(SystemDashboardQueryCriteria criteria) {
		return branchSchemeDao.findSystemDashboard(criteria);
	}

	@Override
	public Long getSystemDashboardCountByCriteria(SystemDashboardQueryCriteria criteria) {
		return branchSchemeDao.getSystemDashboardCountByCriteria(criteria);
	}

}
