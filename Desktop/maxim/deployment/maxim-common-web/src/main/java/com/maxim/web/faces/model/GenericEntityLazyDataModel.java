package com.maxim.web.faces.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.maxim.entity.AbstractEntity;

@SuppressWarnings("serial")
public class GenericEntityLazyDataModel extends LazyDataModel<AbstractEntity> {

    protected GenericDataModelQuery dataModelQuery;
    protected List<? extends AbstractEntity> datasource;
    protected int totalCount;

    public GenericEntityLazyDataModel(GenericDataModelQuery dataModelQuery) {
        this.dataModelQuery = dataModelQuery;
    }

    @Override
    public AbstractEntity getRowData(String rowKey) {
        for (AbstractEntity entity : datasource) {
            if (entity.getId().toString().equals(rowKey))
                return entity;
        }

        return null;
    }

    @Override
    public Object getRowKey(AbstractEntity entity) {
        return entity.getId().toString();
    }

    @Override
    public List<AbstractEntity> load(int first, int pageSize, List<SortMeta> multiSortMeta,
            Map<String, Object> filters) {
        return super.load(first, pageSize, multiSortMeta, filters);
    }

    @Override
    public List<AbstractEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {
        List<AbstractEntity> data = new ArrayList<AbstractEntity>();

        Map<String, String> sortFieldAndOrder = null;
        if (sortField != null) {
            sortFieldAndOrder = new HashMap<String, String>();
            sortFieldAndOrder.put(sortField, SortOrder.DESCENDING.equals(sortOrder) ? "desc" : "asc");
        }

        findDatas(first, pageSize, sortFieldAndOrder, filters);
        data.addAll(datasource);
        return data;
    }

    protected void findDatas(int first, int pageSize, Map<String, String> sortFieldAndOrder,
            Map<String, Object> filters) {
        this.datasource = new ArrayList<AbstractEntity>(dataModelQuery.getDataSource(first, pageSize));
        this.setRowCount(dataModelQuery.getTotalCount());
    }

    public GenericDataModelQuery getDataModelQuery() {
        return dataModelQuery;
    }

    public void setDataModelQuery(GenericDataModelQuery dataModelQuery) {
        this.dataModelQuery = dataModelQuery;
    }

    public List<? extends AbstractEntity> getDatasource() {
        return datasource;
    }

}
