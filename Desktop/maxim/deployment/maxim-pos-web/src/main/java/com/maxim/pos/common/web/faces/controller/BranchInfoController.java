package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.exception.PosException;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.BranchInfoQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.BranchInfoDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.sales.service.BranchInfoService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Controller
@Scope("viewScope")
public class BranchInfoController implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -945864221147020276L;

    @Autowired
    private BranchInfoService branchInfoService;
    private GenericEntityLazyDataModel dataModel;
    private BranchInfo branchInfo;
    private String password;
    @Autowired
    private ApplicationSettingService applicationSettingService;

    public void add() {
        branchInfo = new BranchInfo();
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Branch info Saved")
    public void save() throws Exception {
        Auditer.audit(branchInfo, UserDetailsService.getUser());
//   	String password = EncryptionUtil.aesEncrypt(branchInfo.getPassword(), encryptKey);
//   	branchInfo.setPassword(password);
        if (branchInfo.getClientPort() != null) {
            if (branchInfo.getClientPort() > 65535 || branchInfo.getClientPort() < 0) {
                throw new PosException("client Port Rang 0-65535");
            }
        }
        if (StringUtils.length(branchInfo.getClientHost()) > 200) {
            throw new PosException("host max length 200");
        }

        if (StringUtils.length(branchInfo.getPassword()) > 200) {
            throw new PosException("password max length 200");
        }

        branchInfoService.save(branchInfo);
    }


    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Branch info deleted")
    public void delete() {
        branchInfoService.delete(branchInfo.getId());
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Connection Test")
    public void test() {
        if (branchInfo.getClientType() == ClientType.ORACLE
                ||
                org.apache.commons.lang.StringUtils.startsWith(branchInfo.getClientType().name(), "SQLPOS")
                ||
                branchInfo.getClientType() == ClientType.SQLSERVER) {
            try {
        		BranchScheme scheme = new BranchScheme();
        		scheme.setBranchInfo(branchInfo);

                Connection conn = applicationSettingService.getJDBCConection(scheme, false);
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                        LogUtils.printException(null, e);
                    }

                }
            } catch (Exception e) {
                throw new PosException("test connection failure");
            }
        } else if (branchInfo.getClientType() == ClientType.CSV || branchInfo.getClientType() == ClientType.DBF || branchInfo.getClientType() == ClientType.TEXT) { 
        
    		String user = branchInfo.getUser() ;
    		String password = branchInfo.getPassword() ;

    		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, password);
    		
    		String brachDirName =  "smb://"+branchInfo.getClientHost() + "/";
    		try {
    			
    			SmbFile directory = new SmbFile(brachDirName, auth);
    			directory.list();
    		} catch (MalformedURLException | SmbException e1) {
        		LogUtils.printException("connet error ", e1);
                throw new PosException("test connection failure");
    	    }
        } else {
            throw new PosException("ClientType Unable Test");
        }
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            BranchInfoQueryCriteria criteria = new BranchInfoQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new BranchInfoDataModelQuery(branchInfoService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public BranchInfo getBranchInfo() {
        return branchInfo;
    }

    public void setBranchInfo(BranchInfo branchInfo) {
        this.branchInfo = branchInfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
