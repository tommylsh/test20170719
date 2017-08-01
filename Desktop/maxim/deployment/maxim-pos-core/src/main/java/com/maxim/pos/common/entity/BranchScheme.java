package com.maxim.pos.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;

@Entity
@Table(name = "POLL_BRANCH_SCHEME")
public class BranchScheme extends AbstractEntity {

    private static final long serialVersionUID = 3367570103210829643L;

    private Date businessDate;
    private boolean reRun;

    private BranchInfo branchInfo;
    private PollSchemeType pollSchemeType;
    private Direction direction;
//	private String branchCode;
    private String pollSchemeName;
    private String pollSchemeDesc;
    private Date startTime;
    private Date endTime;
    private boolean enabled;

    private BranchMaster branchMaster;
    private SchemeScheduleJob schemeScheduleJob;
	private TaskJobLog taskLog;
	private TaskJobLog dependOnTaskLog;
	private SchemeJobLog schemeJobLog;

    @Override
    @Id
    @Column(name = "POLL_BRANCH_SCHEME_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POLL_BRANCH_INFO_ID", nullable = false)
    public BranchInfo getBranchInfo() {
        return branchInfo;
    }

    public void setBranchInfo(BranchInfo branchInfo) {
        this.branchInfo = branchInfo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POLL_BRANCH_MASTER_ID", nullable = false)
    public BranchMaster getBranchMaster() {
        return branchMaster;
    }

    public void setBranchMaster(BranchMaster branchMaster) {
        this.branchMaster = branchMaster;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "DIRECTION")
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Column(name = "POLL_SCHEME_NAME", length = 100)
    public String getPollSchemeName() {
        return pollSchemeName;
    }

    public void setPollSchemeName(String pollSchemeName) {
        this.pollSchemeName = pollSchemeName;
    }

    @Column(name = "POLL_SCHEME_DESC", length = 200)
    public String getPollSchemeDesc() {
        return pollSchemeDesc;
    }

    public void setPollSchemeDesc(String pollSchemeDesc) {
        this.pollSchemeDesc = pollSchemeDesc;
    }

//    @Column(name = "BRANCH_CODE", length = 10)
//    public String getBranchCode() {
//		return branchCode;
//	}
//
//	public void setBranchCode(String branchCode) {
//		this.branchCode = branchCode;
//	}



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

    @Column(name = "IS_ENABLED")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    @Column(name = "POLL_SCHEME_TYPE")
    @Enumerated(EnumType.STRING)
	public PollSchemeType getPollSchemeType() {
		return pollSchemeType;
	}

	public void setPollSchemeType(PollSchemeType pollSchemeType) {
		this.pollSchemeType = pollSchemeType;
	}
	
	
	@Transient 
	public SchemeScheduleJob getSchemeScheduleJob() {
		return schemeScheduleJob;
	}

	public void setSchemeScheduleJob(SchemeScheduleJob schemeScheduleJob) {
		this.schemeScheduleJob = schemeScheduleJob;
	}
	
	@Transient
	public TaskJobLog getTaskLog() {
		return taskLog;
	}

	public void setTaskLog(TaskJobLog taskLog) {
		this.taskLog = taskLog;
	}
	@Transient
	public TaskJobLog getDependOnTaskLog() {
		return dependOnTaskLog;
	}

	public void setDependOnTaskLog(TaskJobLog dependOnTaskLog) {
		this.dependOnTaskLog = dependOnTaskLog;
	}



	@Transient
	public SchemeJobLog getSchemeJobLog() {
		return schemeJobLog;
	}

	public void setSchemeJobLog(SchemeJobLog schemeJobLog) {
		this.schemeJobLog = schemeJobLog;
	}
	
	@Transient
    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }
    @Transient
    public boolean isReRun() {
        return reRun;
    }

    public void setReRun(boolean reRun) {
        this.reRun = reRun;
    }

    @Override
    public String toString() {
        return "BranchScheme{" +
                "branchInfo=" + branchInfo +
                ", pollSchemeType=" + pollSchemeType +
                ", direction=" + direction +
                ", pollSchemeName='" + pollSchemeName + '\'' +
                ", pollSchemeDesc='" + pollSchemeDesc + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", enabled=" + enabled +
                ", branchMaster=" + branchMaster +
                ", schemeScheduleJob=" + schemeScheduleJob +
                '}';
    }
}
