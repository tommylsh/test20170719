package com.maxim.pos.common.entity;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;

import javax.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "TASK_JOB_LOG")
@org.hibernate.annotations.Entity(dynamicInsert=true,dynamicUpdate=true)
public class TaskJobLog extends AbstractEntity {
    private static final long serialVersionUID = 1L;


//    private SchemeJobLog schemeJobLog;
    private Long scheduleJobId;
	private Long pollSchemeJobLogId;
    private String taskName;
    private Long pollSchemeID;
    private Long dependOn;
    private String branchCode;
    private String pollSchemeName;
    private PollSchemeType pollSchemeType;
    private Direction direction;
    private Date startTime;
    private Date endTime;
    private LatestJobInd lastestJobInd;
    private TaskProcessStatus status;
    private String errorCode;
    private String errorMsg;
    private Long pollBranchId;
    private String pollSchemedesc;

    private Set<TaskJobLogDetail> taskJobLogDetails;
    private Set<TaskJobExceptionDetail> taskJobExceptionDetails;

//    private BranchScheme branchScheme;

    @Transient
	private TaskJobLog lastTaskJobLog;

    @Transient
	private Long lastTaskJobLogId;
    
    @Transient
	private Long lastDependTaskJobLogId;

	@Override
    @Id
    @Column(name = "TASK_JOB_LOG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @Column(name = "SCHEDULE_JOB_ID")
    public Long getScheduleJobId() {
        return scheduleJobId;
    }

    public void setScheduleJobId(Long scheduleJobId) {
        this.scheduleJobId = scheduleJobId;
    }


//
//    public void setSchemeScheduleJob(SchemeScheduleJob schemeScheduleJob) {
//        this.schemeScheduleJob = schemeScheduleJob;
//        if(schemeScheduleJob!=null){
//            setScheduleJobId(schemeScheduleJob.getId());
//        }
//
//    }

    @Column(name = "TASK_NAME")
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Column(name = "POLL_SCHEME_ID")
    public Long getPollSchemeID() {
        return pollSchemeID;
    }

    public void setPollSchemeID(Long pollSchemeID) {
        this.pollSchemeID = pollSchemeID;
    }

    @Column(name = "DEPEND_ON")
    public Long getDependOn() {
        return dependOn;
    }

    public void setDependOn(Long dependOn) {
        this.dependOn = dependOn;
    }

    @Column(name = "BRANCH_CODE")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @Column(name = "POLL_SCHEME_NAME")
    public String getPollSchemeName() {
        return pollSchemeName;
    }

    public void setPollSchemeName(String pollSchemeName) {
        this.pollSchemeName = pollSchemeName;
    }

    @Column(name = "POLL_SCHEME_TYPE")
    @Enumerated(EnumType.STRING)
    public PollSchemeType getPollSchemeType() {
        return pollSchemeType;
    }

    public void setPollSchemeType(PollSchemeType pollSchemeType) {
        this.pollSchemeType = pollSchemeType;
    }

    @Column(name = "DIRECTION")
    @Enumerated(EnumType.STRING)
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
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

    @Column(name = "LASTTEST_JOB_IND")
    @Enumerated(EnumType.STRING)
    public LatestJobInd getLastestJobInd() {
        return lastestJobInd;
    }

    public void setLastestJobInd(LatestJobInd lastestJobInd) {
        this.lastestJobInd = lastestJobInd;
    }

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    public TaskProcessStatus getStatus() {
        return status;
    }

    public void setStatus(TaskProcessStatus status) {
        this.status = status;
    }

    @Column(name = "ERROR_CODE")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Column(name = "ERROR_MSG")
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Column(name = "POLL_BRANCH_ID")
    public Long getPollBranchId() {
        return pollBranchId;
    }

    public void setPollBranchId(Long pollBranchId) {
        this.pollBranchId = pollBranchId;
    }

    @Column(name = "POLL_SCHEME_DESC")
    public String getPollSchemedesc() {
        return pollSchemedesc;
    }

    public void setPollSchemedesc(String pollSchemedesc) {
        this.pollSchemedesc = pollSchemedesc;
    }

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "taskJobLog")
    public Set<TaskJobLogDetail> getTaskJobLogDetails() {
        return taskJobLogDetails;
    }

    public void setTaskJobLogDetails(Set<TaskJobLogDetail> taskJobLogDetails) {
        this.taskJobLogDetails = taskJobLogDetails;
    }

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "taskJobLog")
    public Set<TaskJobExceptionDetail> getTaskJobExceptionDetails() {
        return taskJobExceptionDetails;
    }

    public void setTaskJobExceptionDetails(Set<TaskJobExceptionDetail> taskJobExceptionDetails) {
        this.taskJobExceptionDetails = taskJobExceptionDetails;
    }
    
    
    @Column(name = "POLL_SCHEME_JOB_LOG_ID")
    public Long getPollSchemeJobLogId() {
		return pollSchemeJobLogId;
	}

	public void setPollSchemeJobLogId(Long pollSchemeJobLogId) {
		this.pollSchemeJobLogId = pollSchemeJobLogId;
	}


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "POLL_SCHEME_JOB_LOG_ID", nullable = true)
//	public SchemeJobLog getSchemeJobLog() {
//		return schemeJobLog;
//	}
//
//	public void setSchemeJobLog(SchemeJobLog schemeJobLog) {
//		this.schemeJobLog = schemeJobLog;
//	}

//	@OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "POLL_SCHEME_ID",insertable = false,updatable = false)
//    public BranchScheme getBranchScheme() {
//        return branchScheme;
//    }
//
//    public void setBranchScheme(BranchScheme branchScheme) {
//        this.branchScheme = branchScheme;
//    }
    
    @Transient
    public TaskJobLog getLastTaskJobLog() {
		return lastTaskJobLog;
	}

	public void setLastTaskJobLog(TaskJobLog lastTaskJobLog) {
		this.lastTaskJobLog = lastTaskJobLog;
	}

    @Transient
    public Long getLastTaskJobLogId() {
		return lastTaskJobLogId;
	}

	public void setLastTaskJobLogId(Long lastTaskJobLogId) {
		this.lastTaskJobLogId = lastTaskJobLogId;
	}

    @Transient
	public Long getLastDependTaskJobLogId() {
		return lastDependTaskJobLogId;
	}

	public void setLastDependTaskJobLogId(Long lastDependTaskJobLogId) {
		this.lastDependTaskJobLogId = lastDependTaskJobLogId;
	}
}
