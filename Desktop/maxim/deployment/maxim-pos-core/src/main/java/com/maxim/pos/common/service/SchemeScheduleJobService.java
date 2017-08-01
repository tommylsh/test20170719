package com.maxim.pos.common.service;

import java.util.List;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.value.CommonCriteria;

public interface SchemeScheduleJobService {

    String BEAN_NAME = "schemeScheduleJobService";

    void save(SchemeScheduleJob schemeScheduleJob);

    void delete(Long scheduleJobId);

    List<SchemeScheduleJob> findSchemeScheduleJobByCriteria(CommonCriteria criteria);

    Long getSchemeScheduleJobCountByCriteria(CommonCriteria criteria);

    SchemeScheduleJob getSchemeScheduleJob(BranchScheme branchScheme);
}
