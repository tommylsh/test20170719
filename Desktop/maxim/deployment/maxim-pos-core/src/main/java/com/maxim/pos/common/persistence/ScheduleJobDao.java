package com.maxim.pos.common.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.pos.common.entity.SchemeScheduleJob;

@Repository("scheduleJobDao")
@Transactional
public class ScheduleJobDao extends HibernateEntityDAO<SchemeScheduleJob, Long> {

//    public static final String HQL_findAllSchedules = "findAllSchedules";
	
	public ScheduleJobDao()
	{
//		super(SchemeScheduleJob.class, Long.class);
		super();
	}

    public SchemeScheduleJob findById(Long id) 
    {
    	return super.findByKey(id);
    }
    
}
