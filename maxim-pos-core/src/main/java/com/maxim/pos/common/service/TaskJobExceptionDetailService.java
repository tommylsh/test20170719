package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;
import com.maxim.pos.common.enumeration.Severity;
import com.maxim.pos.common.value.CommonCriteria;

import java.util.List;

public interface TaskJobExceptionDetailService {

    String BEAN_NAME = "taskJobExceptionDetailService";

    void save(TaskJobExceptionDetail taskJobExceptionDetail);

    void delete(Long taskJobExceptionDetailId);

    List<TaskJobExceptionDetail> findTaskJobExceptionDetailByCriteria(CommonCriteria criteria);

    Long getTaskJobExceptionDetailCountByCriteria(CommonCriteria criteria);
    
    List<TaskJobExceptionDetail> findTaskJobExeptionDetailByStatusAndSeverity(ExceptionDetailStatus status, Severity severity);

    void update(TaskJobExceptionDetail taskJobExceptionDetail);
}
