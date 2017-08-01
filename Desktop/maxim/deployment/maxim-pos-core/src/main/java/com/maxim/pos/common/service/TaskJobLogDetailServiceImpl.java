package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.TaskJobLogDetail;
import com.maxim.pos.common.persistence.TaskJobLogDetailDao;
import com.maxim.pos.common.value.CommonCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service(TaskJobLogDetailService.BEAN_NAME)
public class TaskJobLogDetailServiceImpl implements TaskJobLogDetailService {

    @Autowired
    private TaskJobLogDetailDao taskJobLogDetailDao;

    @Override
    public void save(TaskJobLogDetail taskJobLogDetail) {
        if (taskJobLogDetail == null) {
            throw new RuntimeException("[Validation failed] - this argument [taskJobLogDetail] is required; it must not be null");
        }
        taskJobLogDetailDao.save(taskJobLogDetail);
    }

    @Override
    public void delete(Long taskJobLogDetailId) {
        if (taskJobLogDetailId == null) {
            throw new RuntimeException("[Validation failed] - this argument [taskJobLogDetailId] is required; it must not be null");
        }
        TaskJobLogDetail taskJobLogDetail = taskJobLogDetailDao.getById(taskJobLogDetailId);
        if (taskJobLogDetail == null) {
            throw new RuntimeException("The record which taskJobLogDetailId=" + taskJobLogDetailId + " is not exist.");
        }
        taskJobLogDetailDao.delete(taskJobLogDetail);
    }

    @Override
    public List<TaskJobLogDetail> findTaskJobLogDetailByCriteria(CommonCriteria criteria) {
        return taskJobLogDetailDao.findTaskJobLogDetailByCriteria(criteria);
    }

    @Override
    public Long getTaskJobLogDetailCountByCriteria(CommonCriteria criteria) {
        return taskJobLogDetailDao.getTaskJobLogDetailCountByCriteria(criteria);
    }

}
