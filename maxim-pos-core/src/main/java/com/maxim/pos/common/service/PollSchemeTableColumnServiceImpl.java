package com.maxim.pos.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.persistence.PollSchemeTableColumnDao;
import com.maxim.pos.common.value.CommonCriteria;

@Transactional
@Service(PollSchemeTableColumnService.BEAN_NAME)
public class PollSchemeTableColumnServiceImpl implements PollSchemeTableColumnService {

    @Autowired
    private PollSchemeTableColumnDao pollSchemeTableColumnDao;


    @Override
    public void save(SchemeTableColumn schemeTableColumn) {
        if (schemeTableColumn == null) {
        throw new RuntimeException("[Validation failed] - this argument [schemeTableColumn] is required; it must not be null");
        }
        pollSchemeTableColumnDao.save(schemeTableColumn);
    }

    @Override
    public void delete(Long schemeTableColumnId) {
        pollSchemeTableColumnDao.delete(schemeTableColumnId);

//        if (schemeTableColumnId == null) {
//            throw new RuntimeException("[Validation failed] - this argument [schemeTableColumnId] is required; it must not be null");
//        }
//        SchemeTableColumn schemeTableColumn = pollSchemeTableColumnDao.getById(schemeTableColumnId);
//        if (schemeTableColumn == null) {
//            throw new RuntimeException("The record which schemeTableColumnId=" + schemeTableColumnId + " is not exist.");
//        }
//        pollSchemeTableColumnDao.delete(schemeTableColumn);
//        pollSchemeInfoDao.getById(schemeTableColumn.getSchemeInfo().getId()).getSchemeTableColumns().size();
//        pollSchemeInfoDao.update(pollSchemeInfoDao.getById(schemeTableColumn.getSchemeInfo().getId()));

    }

    @Override
    public List<SchemeTableColumn> findSchemeTableColumnByCriteria(CommonCriteria criteria) {
        return pollSchemeTableColumnDao.findSchemeTableColumnByCriteria(criteria);
    }
    
    @Override
    public Long getSchemeTableColumnCountByCriteria(CommonCriteria criteria) {
        return pollSchemeTableColumnDao.getSchemeTableColumnCountByCriteria(criteria);
    }

}
