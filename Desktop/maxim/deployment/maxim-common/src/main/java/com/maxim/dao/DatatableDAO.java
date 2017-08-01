package com.maxim.dao;

import java.io.Serializable;

import com.maxim.datatable.DatatableDaoCmd;
import com.maxim.datatable.Page;

public interface DatatableDAO {

    public <T extends Serializable> Page<T> getPage(DaoCmd cmd, Class<?> clazz, Integer maxResult, Integer startFrom);
    
    public <T extends Serializable> Page<T> getPage(DatatableDaoCmd cmd, Class<?> clazz);

    public int getCount(DaoCmd cmd);
}
