package com.maxim.pos.master.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "BRANCH_INVENTORY_INFO")
public class BranchInventoryInfo {

	private Date businessDate;
    private String branchCode;
    private Date lastUpdateDateTime;
    
    @Id
    @Column(name = "BUSINESS_DATE")
    public Date getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

    @Id
    @Column(name = "BRANCH_CODE")
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

    @Column(name = "LAST_UPDATE_DATE_TIME")
	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}
	public void setLastUpdateDateTime(Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}

}
