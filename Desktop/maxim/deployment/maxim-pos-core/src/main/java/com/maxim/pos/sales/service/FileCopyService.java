package com.maxim.pos.sales.service;

import org.slf4j.Logger;

import com.maxim.pos.common.entity.BranchScheme;

public interface FileCopyService {
	public void fileCopy(BranchScheme branchScheme, Logger logger);
}
