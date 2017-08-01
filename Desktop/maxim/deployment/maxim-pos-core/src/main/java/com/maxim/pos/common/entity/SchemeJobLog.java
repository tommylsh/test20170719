package com.maxim.pos.common.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.JobProcessStatus;
import com.maxim.pos.common.enumeration.LatestJobInd;

@Entity
@Table(name = "POLL_SCHEME_JOB_LOG")
public class SchemeJobLog extends AbstractEntity {

	private static final long serialVersionUID = -8010950804042236300L;

	private Long scheduleJobId;
	private Date startTime;
	private Date endTime;
	private int numberOfRecordProcessed;
	private LatestJobInd lastJobInd;
	private JobProcessStatus status;
	private List<BranchScheme> branchSchemeList;
	private Map<String, List<SchemeInfo>> schemeInfoListMap ;

	@Override
	@Id
	@Column(name = "POLL_SCHEME_JOB_LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@Column(name = "SCHEDULE_JOB_ID", nullable = false)
	public Long getScheduleJobId() {
		return scheduleJobId;
	}

	public void setScheduleJobId(Long scheduleJobId) {
		this.scheduleJobId = scheduleJobId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_TIME")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_TIME")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "NUMBER_OF_RECORD_PROCESSED", nullable = false)
	public int getNumberOfRecordProcessed() {
		return numberOfRecordProcessed;
	}

	public void setNumberOfRecordProcessed(int numberOfRecordProcessed) {
		this.numberOfRecordProcessed = numberOfRecordProcessed;
	}

	@Column(name = "LAST_JOB_IND", length = 10)
	@Enumerated(EnumType.STRING)
	public LatestJobInd getLastJobInd() {
		return lastJobInd;
	}

	public void setLastJobInd(LatestJobInd lastJobInd) {
		this.lastJobInd = lastJobInd;
	}

	@Column(name = "STATUS", length = 20)
	@Enumerated(EnumType.STRING)
	public JobProcessStatus getStatus() {
		return status;
	}

	public void setStatus(JobProcessStatus status) {
		this.status = status;
	}

	
	@Transient
	public List<BranchScheme> getBranchSchemeList() {
		return branchSchemeList;
	}

	public void setBranchSchemeList(List<BranchScheme> branchSchemeList) {
		this.branchSchemeList = branchSchemeList;
	}

	@Transient
	public Map<String, List<SchemeInfo>> getSchemeInfoListMap() {
		return schemeInfoListMap;
	}

	public void setSchemeInfoListMap(Map<String, List<SchemeInfo>> schemeInfoListMap) {
		this.schemeInfoListMap = schemeInfoListMap;
	}


}
