package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;

import com.maxim.pos.common.entity.SchemeInfo;

import com.maxim.pos.common.exception.PosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.service.PollSchemeTableColumnService;
import com.maxim.pos.common.value.SchemeTableColumnQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.SchemeTableColumnDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller
@Scope("viewScope")
public class SchemeTableColumnController implements Serializable {

    @Autowired
    private PollSchemeTableColumnService pollSchemeTableColumnService;

    private GenericEntityLazyDataModel dataModel;
    private SchemeTableColumn schemeTableColumn;

    private Long schemeInfoId;

    public void add() {
        schemeTableColumn = new SchemeTableColumn();
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save SchemeTableColumn")
    public void save() {
        if (schemeInfoId != null) {
            Auditer.audit(schemeTableColumn, UserDetailsService.getUser());
            SchemeInfo schemeInfo = new SchemeInfo();
            schemeInfo.setId(schemeInfoId);
            schemeTableColumn.setSchemeInfo(schemeInfo);
            pollSchemeTableColumnService.save(schemeTableColumn);
        } else {
            throw new PosException("SchemeInfo is empty");
        }
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete SchemeTableColumn")
    public void delete() {
        pollSchemeTableColumnService.delete(schemeTableColumn.getId());
//        pollSchemeTableColumnService.delete(schemeTableColumn);
    }

    public GenericEntityLazyDataModel getDataModel() {
    	SchemeTableColumnQueryCriteria criteria=null;
    	if (dataModel == null) {
    		criteria = new SchemeTableColumnQueryCriteria();
            criteria.setSchemeInfoId(schemeInfoId);
            dataModel = new GenericEntityLazyDataModel(new SchemeTableColumnDataModelQuery(pollSchemeTableColumnService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public SchemeTableColumn getSchemeTableColumn() {
        return schemeTableColumn;
    }

    public void setSchemeTableColumn(SchemeTableColumn schemeTableColumn) {
        this.schemeTableColumn = schemeTableColumn;
    }

	public Long getSchemeInfoId() {
		return schemeInfoId;
	}

	public void setSchemeInfoId(Long schemeInfoId) {
		this.schemeInfoId = schemeInfoId;
	}

    
}
