package com.maxim.web.faces.model;

import java.util.List;

import com.maxim.entity.AbstractEntity;

public interface GenericDataModelQuery {

	public List<? extends AbstractEntity> getDataSource(int first, int pageSize);

	public int getTotalCount();

}
