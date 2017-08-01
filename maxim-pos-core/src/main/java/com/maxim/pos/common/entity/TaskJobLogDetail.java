package com.maxim.pos.common.entity;

import javax.persistence.*;

import com.maxim.entity.AbstractEntity;


@Entity
@Table(name = "TASK_JOB_LOG_DETAIL")
public class TaskJobLogDetail extends AbstractEntity implements Comparable<TaskJobLogDetail> {
	private static final long serialVersionUID = 1L;
	
	private TaskJobLog taskJobLog;
	private String source;
	private String destination;
	private Integer numOfRecProcessed;
	private Integer numOfRecInsert;
	private Integer numOfRecUpdate;
	private Integer numOfRecDelete;
	
    @Override
    @Id
    @Column(name = "TASK_JOB_LOG_DETAIL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "TASK_JOB_LOG_ID", nullable = false)
	public TaskJobLog getTaskJobLog() {
		return taskJobLog;
	}
	public void setTaskJobLog(TaskJobLog taskJobLog) {
		this.taskJobLog = taskJobLog;
	}
	@Column(name = "SOURCE")
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Column(name = "DESTINATION")
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	@Column(name = "NUM_OF_REC_PROCESSED")
	public Integer getNumOfRecProcessed() {
		return numOfRecProcessed;
	}
	public void setNumOfRecProcessed(Integer numOfRecProcessed) {
		this.numOfRecProcessed = numOfRecProcessed;
	}
	@Column(name = "NUM_OF_REC_INSERT")
	public Integer getNumOfRecInsert() {
		return numOfRecInsert;
	}
	public void setNumOfRecInsert(Integer numOfRecInsert) {
		this.numOfRecInsert = numOfRecInsert;
	}
	@Column(name = "NUM_OF_REC_UPDATE")
	public Integer getNumOfRecUpdate() {
		return numOfRecUpdate;
	}
	public void setNumOfRecUpdate(Integer numOfRecUpdate) {
		this.numOfRecUpdate = numOfRecUpdate;
	}
	@Column(name = "NUM_OF_REC_DELETE")
	public Integer getNumOfRecDelete() {
		return numOfRecDelete;
	}
	public void setNumOfRecDelete(Integer numOfRecDelete) {
		this.numOfRecDelete = numOfRecDelete;
	}
    @Override
    public int compareTo(TaskJobLogDetail o) {
        return source.compareTo(o.source);
    }
	
	
}