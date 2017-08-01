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
@Table(name = "POLL_EOD_CONTROL")
public class PollEodControl extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_PENDING	= "P"; 
	public static final String STATUS_COMPLETE 	= "C"; 
	public static final String STATUS_WAIT_ST	= "S"; 
	public static final String STATUS_STAGE		= "T"; 
	

	private String branchCode;
	private Date businessDate;
	private String status;
	
	@Override
	@Id
	@Column(name = "POLL_EOD_CONTROL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}
	
	@Column(name = "BRANCH_CODE")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BUSINESS_DATE")
	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}
	
	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}