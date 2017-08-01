package com.maxim.data;

import java.util.Date;

import com.maxim.datatable.AbstractDatatableDTO;
public class BaseDTO extends AbstractDatatableDTO {

    /** 
     *
     */
    private static final long serialVersionUID = 3761458575768025556L;
    private String createUser;
    private Date createDate;
    private String lastUpdUser;
    private Date lastUpdDate;
    
//    private String status;
    

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLastUpdUser() {
        return lastUpdUser;
    }

    public void setLastUpdUser(String lastUpdUser) {
        this.lastUpdUser = lastUpdUser;
    }

    public Date getLastUpdDate() {
        return lastUpdDate;
    }

    public void setLastUpdDate(Date lastUpdDate) {
        this.lastUpdDate = lastUpdDate;
    }

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    */

}
