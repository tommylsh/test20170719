package com.maxim.pos.common.entity;

import com.maxim.entity.AbstractEntity;

import javax.persistence.*;

@Entity
@Table(name = "POLL_BRANCH_MASTER ",uniqueConstraints = {
	      @UniqueConstraint(columnNames = {"BRANCH_CODE"})
	})
public class BranchMaster extends AbstractEntity {

    private static final long serialVersionUID = 3707873728287513546L;

    private String branchCode;
    private String branchCname;
    private String branchEname;
    private String branchType;

    private String mappingBranchCode;
    
    @Override
    @Id
    @Column(name = "POLL_BRANCH_MASTER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

   
    @Column(name = "BRANCH_CODE", length = 10)
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @Column(name = "BRANCH_CNAME", length = 50)
    public String getBranchCname() {
        return branchCname;
    }

    public void setBranchCname(String branchCname) {
        this.branchCname = branchCname;
    }

    @Column(name = "BRANCH_ENAME", length = 50)
    public String getBranchEname() {
        return branchEname;
    }

    public void setBranchEname(String branchEname) {
        this.branchEname = branchEname;
    }

    @Column(name = "BRANCH_TYPE", length = 4)
    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

//    @Transient
    @Column(name = "MAPPING_BRANCH_CODE", length = 50)
    public String getMappingBranchCode() {
        return mappingBranchCode;
    }

    public void setMappingBranchCode(String mappingBranchCode) {
        this.mappingBranchCode = mappingBranchCode;
    }
}