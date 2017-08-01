package com.maxim.web.faces.model;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.model.SortOrder;

import com.maxim.entity.AbstractEntity;

public class GenericDataModelSorter implements Comparator<AbstractEntity> {

    private String sortField;

    private SortOrder sortOrder;

    public GenericDataModelSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compare(AbstractEntity entity1, AbstractEntity entity2) {
        try {
            String value1 = BeanUtils.getProperty(entity1, this.sortField) ;
            value1 = (value1 == null) ? "" : value1;
            String value2 = BeanUtils.getProperty(entity2, this.sortField) ;
            value2 = (value2 == null) ? "" : value2;

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
