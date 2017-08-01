package com.maxim.pos.common.value;

import java.util.Date;


public class SystemDashboardQueryCriteria extends CommonCriteria {
	
	private static final long serialVersionUID = 1L;
	private Date startTime;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
}
