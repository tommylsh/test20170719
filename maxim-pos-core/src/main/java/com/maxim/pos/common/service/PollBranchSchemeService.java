package com.maxim.pos.common.service;

import java.util.List;

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

public interface PollBranchSchemeService {
	
	
	public BranchScheme savePollBranchScheme(BranchScheme schema);
	
	/**
	 * query by  pollSchemeDirection and pollSchemeType
	 * @param schemeScheduleJob
	 * @return BranchScheme list
	 */
	public List<BranchScheme> getBranchSchemeByScheduleJob(SchemeScheduleJob schemeScheduleJob);
	
	public BranchScheme getBranchScheme(PollSchemeType pollSchemeType,Direction direction,ClientType type,String branchCode);

	public List<BranchScheme> findBranchSchemeByCriteria(CommonCriteria criteria);
	public Long getBranchSchemeCountByCriteria(CommonCriteria criteria);

	public void delete(Long branchSchemeId);
	
	public List<TaskJobLog> findEodProcess(TaskJobLogQueryCriteria criteria);
	public Long getEodProcessCountByCriteria(TaskJobLogQueryCriteria criteria);

	public List<SystemDashboard> findSystemDashboard(SystemDashboardQueryCriteria criteria);
	public Long getSystemDashboardCountByCriteria(SystemDashboardQueryCriteria criteria);
}
