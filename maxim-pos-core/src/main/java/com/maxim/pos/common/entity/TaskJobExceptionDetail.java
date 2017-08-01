package com.maxim.pos.common.entity;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;

@Entity
@Table(name = "TASK_JOB_EXECEPTION_DETAIL")
public class TaskJobExceptionDetail extends AbstractEntity implements Comparable<TaskJobExceptionDetail> {
	private static final long serialVersionUID = 1L;

	private TaskJobLog taskJobLog;
	private String source;
	private String destination;
	private String exceptionContent;
	
	/**
	 * severity
	 * 
	 * 1 -> Critical
	 * 2 -> Error
	 * 3 -> Warning
	 * 4 -> Info
	 * 5 -> Trace
	 * 
	 */
	private Integer severity;
	
	/**
	 * status
	 * 
	 * P -> Pending
	 * S -> Sent
	 * D -> Done
	 * 
	 */
	private ExceptionDetailStatus status;
	
	@Override
	@Id
	@Column(name = "TASK_JOB_EXECEPTION_DETAIL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "EXCEPTION_CONTENT", columnDefinition = "nvarchar(MAX)", nullable = true)
	public String getExceptionContent() {
		return exceptionContent;
	}

	public void setExceptionContent(String exceptionContent) {
		this.exceptionContent = exceptionContent;
	}

	@Override
	public int compareTo(TaskJobExceptionDetail o) {
		return source.compareTo(o.source);
	}
    @Column(name = "SEVERITY")
//    @Enumerated(EnumType.STRING)
	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
	public ExceptionDetailStatus getStatus() {
		return status;
	}

	public void setStatus(ExceptionDetailStatus status) {
		this.status = status;
	}

	
}
