package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;
import com.maxim.pos.common.enumeration.Severity;
import com.maxim.pos.common.persistence.TaskJobExceptionDetailDao;
import com.maxim.pos.common.value.CommonCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service(TaskJobExceptionDetailService.BEAN_NAME)
public class TaskJobExceptionDetailServiceImpl implements TaskJobExceptionDetailService {

    @Autowired
    private TaskJobExceptionDetailDao taskJobExceptionDetailDao;

    @Override
    public void save(TaskJobExceptionDetail taskJobExceptionDetail) {
        if (taskJobExceptionDetail == null) {
            throw new RuntimeException("[Validation failed] - this argument [taskJobExceptionDetail] is required; it must not be null");
        }
        taskJobExceptionDetailDao.save(taskJobExceptionDetail);
    }

    @Override
    public void delete(Long taskJobExceptionDetailId) {
        if (taskJobExceptionDetailId == null) {
            throw new RuntimeException("[Validation failed] - this argument [taskJobExceptionDetailId] is required; it must not be null");
        }
        TaskJobExceptionDetail taskJobExceptionDetail = taskJobExceptionDetailDao.getById(taskJobExceptionDetailId);
        if (taskJobExceptionDetail == null) {
            throw new RuntimeException("The record which taskJobExceptionDetailId=" + taskJobExceptionDetailId + " is not exist.");
        }
        taskJobExceptionDetailDao.delete(taskJobExceptionDetail);
    }

    @Override
    public List<TaskJobExceptionDetail> findTaskJobExceptionDetailByCriteria(CommonCriteria criteria) {
        return taskJobExceptionDetailDao.findTaskJobExceptionDetailByCriteria(criteria);
    }

    @Override
    public Long getTaskJobExceptionDetailCountByCriteria(CommonCriteria criteria) {
        return taskJobExceptionDetailDao.getTaskJobExceptionDetailCountByCriteria(criteria);
    }

	@Override
	public List<TaskJobExceptionDetail> findTaskJobExeptionDetailByStatusAndSeverity(
			ExceptionDetailStatus status, Severity severity) {
		return taskJobExceptionDetailDao.findTaskJobExeptionDetailByStatusAndSeverity(status,severity);
	}

	@Override
	public void update(TaskJobExceptionDetail taskJobExceptionDetail) {
		taskJobExceptionDetailDao.update(taskJobExceptionDetail);
	}

}
