package com.maxim.pos.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.maxim.entity.AbstractEntity;
@Entity
@Table(name = "v_system_dashboard")
public class SystemDashboard extends AbstractEntity{
	private static final long serialVersionUID = 1L;
	
	private String jobName;
	private String jobDesc;
	private Date startTime;
	private Date endTime;
	private String status;
	private Integer numOfScheme;
	
    @Override
    @Id
    @Column(name = "SYSTEM_DASHBOARD_VIEW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }
    
    @Column(name="JOB_NAME")
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}    
	
	@Column(name="JOB_DESC")
	public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
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
	
	@Column(name="STATUS")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name="numOfScheme")
	public Integer getNumOfScheme() {
		return numOfScheme;
	}
	public void setNumOfScheme(Integer numOfScheme) {
		this.numOfScheme = numOfScheme;
	}
	
}
