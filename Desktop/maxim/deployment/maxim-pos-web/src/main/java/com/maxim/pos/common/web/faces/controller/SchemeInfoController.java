package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.exception.PosException;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.SchemeTableColumnService;
import com.maxim.pos.common.value.SchemeInfoQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.SchemeInfoDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller
@Scope("viewScope")
public class SchemeInfoController implements Serializable {

    @Autowired
    private PollSchemeInfoService schemeInfoService;

    @Autowired
    private PollSchemeInfoService pollSchemeInfoService;

    @Autowired
    private SchemeTableColumnService schemeTableColumnService;

    private GenericEntityLazyDataModel dataModel;
    private SchemeInfo schemeInfo;

    private boolean autoMappingColumn;

    public void add() {
        schemeInfo = new SchemeInfo();
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save SchemeInfo")
    public void save() {
        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getSource()) > 50) {
            throw new PosException("Source max length 50");
        }
        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getDestination()) > 50) {
            throw new PosException("Destination max length 50");
        }
        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getSrcCheckSumCols()) > 200) {
            throw new PosException("Src Check SumCols max length 200");
        }

        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getDestKeyColumns()) > 200) {
            throw new PosException("Dest Key Columns max length 200");
        }
        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getSrcKeyColumns()) > 200) {
            throw new PosException("Src Key Columns max length 200");
        }
        if (org.apache.commons.lang3.StringUtils.length(schemeInfo.getDelimiter()) > 10) {
            throw new PosException("Delimiter max length 10");
        }

        Auditer.audit(schemeInfo, UserDetailsService.getUser());
        schemeInfo.setConsistentStructure(true);
        schemeInfoService.addOrUpdateSchemeInfo(schemeInfo);
        if (autoMappingColumn) {
            try {
                List<SchemeTableColumn> schemeTableColumnList = pollSchemeInfoService.generateSchemeTableColumnData(schemeInfo);
                schemeTableColumnService.saveSchemeTableColumns(schemeTableColumnList);
            } catch (Exception e) {
                // ignored
            }
        }
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete SchemeInfo")
    public void delete() {
        schemeInfoService.delete(schemeInfo.getId());

    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            SchemeInfoQueryCriteria criteria = new SchemeInfoQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new SchemeInfoDataModelQuery(schemeInfoService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public SchemeInfo getSchemeInfo() {
        return schemeInfo;
    }

    public void setSchemeInfo(SchemeInfo schemeInfo) {
        this.schemeInfo = schemeInfo;
    }

    public boolean isAutoMappingColumn() {
        return autoMappingColumn;
    }

    public void setAutoMappingColumn(boolean autoMappingColumn) {
        this.autoMappingColumn = autoMappingColumn;
    }
}
