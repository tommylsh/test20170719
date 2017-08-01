package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.service.SchemeQuartzTaskExecutor;
import com.maxim.pos.common.service.SchemeScheduleJobService;
import com.maxim.pos.common.value.SchemeScheduleJobQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.SchemeScheduleJobDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("viewScope")
public class SchemeScheduleJobController implements Serializable {

    @Autowired
    private SchemeScheduleJobService schemeScheduleJobService;
    @Autowired
    private SchemeQuartzTaskExecutor schemeQuartzTaskExecutor;

    private GenericEntityLazyDataModel dataModel;
    private SchemeScheduleJob schemeScheduleJob;

    public void add() {
        schemeScheduleJob = new SchemeScheduleJob();
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save scheme schedule job")
    public void save() {
        Auditer.audit(schemeScheduleJob, UserDetailsService.getUser());
        schemeScheduleJobService.save(schemeScheduleJob);
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete scheme schedule job")
    public void delete() {
        schemeScheduleJobService.delete(schemeScheduleJob.getId());
    }
    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Re Run schedule job")
    public void reRun(){
        schemeQuartzTaskExecutor.execute(schemeScheduleJob);
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            SchemeScheduleJobQueryCriteria criteria = new SchemeScheduleJobQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new SchemeScheduleJobDataModelQuery(schemeScheduleJobService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public SchemeScheduleJob getSchemeScheduleJob() {
        return schemeScheduleJob;
    }

    public void setSchemeScheduleJob(SchemeScheduleJob schemeScheduleJob) {
        this.schemeScheduleJob = schemeScheduleJob;
    }
}
