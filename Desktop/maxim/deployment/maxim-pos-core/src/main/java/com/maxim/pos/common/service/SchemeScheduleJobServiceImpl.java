package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.persistence.SchemeScheduleJobDao;
import com.maxim.pos.common.value.CommonCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service(SchemeScheduleJobService.BEAN_NAME)
public class SchemeScheduleJobServiceImpl implements SchemeScheduleJobService {

    @Autowired
    private SchemeScheduleJobDao schemeScheduleJobDao;

    @Override
    public void save(SchemeScheduleJob schemeScheduleJob) {
        if (schemeScheduleJob == null) {
            throw new RuntimeException("[Validation failed] - this argument [schemeScheduleJob] is required; it must not be null");
        }
        schemeScheduleJobDao.save(schemeScheduleJob);
    }

    @Override
    public void delete(Long scheduleJobId) {
        if (scheduleJobId == null) {
            throw new RuntimeException("[Validation failed] - this argument [scheduleJobId] is required; it must not be null");
        }
        SchemeScheduleJob scheduleJob = schemeScheduleJobDao.getById(scheduleJobId);
        if (scheduleJob == null) {
            throw new RuntimeException("The record which scheduleJobId=" + scheduleJobId + " is not exist.");
        }
        schemeScheduleJobDao.delete(scheduleJob);
    }

    @Override
    public List<SchemeScheduleJob> findSchemeScheduleJobByCriteria(CommonCriteria criteria) {
        return schemeScheduleJobDao.findSchemeScheduleJobByCriteria(criteria);
    }

    @Override
    public Long getSchemeScheduleJobCountByCriteria(CommonCriteria criteria) {
        return schemeScheduleJobDao.getSchemeScheduleJobCountByCriteria(criteria);
    }

	@Override
	public SchemeScheduleJob getSchemeScheduleJob(BranchScheme branchScheme) {
		return schemeScheduleJobDao.getSchemeScheduleJob(branchScheme);
	}

}
