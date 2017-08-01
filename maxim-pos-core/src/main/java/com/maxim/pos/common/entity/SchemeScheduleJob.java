package com.maxim.pos.common.entity;

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
import javax.persistence.Transient;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;

@Entity
@Table(name = "POLL_SCHEME_SCHEDULE_JOB")
@org.hibernate.annotations.Entity(dynamicInsert=true,dynamicUpdate=true)
public class SchemeScheduleJob extends AbstractEntity {

	private static final long serialVersionUID = 4175627011202558869L;

	private String jobName;
	private String jobDesc;
	private String jobGroup;
	private boolean enable;
	private Direction pollSchemeDirection;
	private PollSchemeType pollSchemeType;
	private String cronExpression;
	
	private Map<String, List<SchemeInfo>> schemeInfoListMap = null ;

	@Override
	@Id
	@Column(name = "SCHEDULE_JOB_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@Column(name = "JOB_NAME", length = 50, nullable = false)
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Column(name = "JOB_GROUP", length = 50, nullable = true)
	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	@Column(name = "CRON_EXPRESSION", length = 50, nullable = false)
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "IS_ENABLE", nullable = false, columnDefinition = "TINYINT", length = 1)
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Column(name = "POLL_SCHEME_DIRECTION", length = 40, nullable = true)
	@Enumerated(EnumType.STRING)
	public Direction getPollSchemeDirection() {
		return pollSchemeDirection;
	}

	public void setPollSchemeDirection(Direction pollSchemeDirection) {
		this.pollSchemeDirection = pollSchemeDirection;
	}

	@Column(name = "POLL_SCHEME_TYPE", length = 20, nullable = true)
	@Enumerated(EnumType.STRING)
	public PollSchemeType getPollSchemeType() {
		return pollSchemeType;
	}

	public void setPollSchemeType(PollSchemeType pollSchemeType) {
		this.pollSchemeType = pollSchemeType;
	}
	@Column(name = "JOB_DESC", length = 200)
	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	
	@Transient 
	public Map<String, List<SchemeInfo>> getSchemeInfoListMap() {
		return schemeInfoListMap;
	}
	public void setSchemeInfoListMap(Map<String, List<SchemeInfo>> schemeInfoListMap) {
		this.schemeInfoListMap = schemeInfoListMap;
	}



	@Override
	public String toString() {
		return "SchemeScheduleJob [jobName=" + jobName + ", jobDesc=" + jobDesc + ", jobGroup=" + jobGroup + ", enable="
				+ enable + ", pollSchemeDirection=" + pollSchemeDirection + ", pollSchemeType=" + pollSchemeType
				+ ", cronExpression=" + cronExpression + "]";
	}
	
	

}