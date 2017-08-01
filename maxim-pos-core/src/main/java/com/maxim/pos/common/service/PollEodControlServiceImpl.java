package com.maxim.pos.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.PollEodControl;
import com.maxim.pos.common.persistence.PollEodControlDao;

@Service("pollEodControlService")
public class PollEodControlServiceImpl implements PollEodControlService{
	
	@Autowired
	private PollEodControlDao pollEodControlDao;
	
	@Override
	public void saveOrUpdateConvertLog(PollEodControl convertLog) {
		pollEodControlDao.saveOrUpdateConvertLog(convertLog);
	}

//	@Override
//	public boolean findConvertLogByBusinessDate(BranchScheme branchScheme) {
//		return pollEodControlDao.findConvertLogByBusinessDate(branchScheme);
//	}
//
//	@Override
//	public PollEodControl findPollEodControl(BranchScheme branchSheme) {
//		return pollEodControlDao.findPollEodControl(branchSheme);
//	}

}
