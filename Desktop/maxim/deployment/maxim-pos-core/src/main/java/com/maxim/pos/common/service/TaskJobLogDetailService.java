package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.TaskJobLogDetail;
import com.maxim.pos.common.value.CommonCriteria;

import java.util.List;

public interface TaskJobLogDetailService {

    String BEAN_NAME = "taskJobLogDetailService";

    void save(TaskJobLogDetail taskJobLogDetail);

    void delete(Long taskJobLogDetailId);

    List<TaskJobLogDetail> findTaskJobLogDetailByCriteria(CommonCriteria criteria);

    Long getTaskJobLogDetailCountByCriteria(CommonCriteria criteria);

}
