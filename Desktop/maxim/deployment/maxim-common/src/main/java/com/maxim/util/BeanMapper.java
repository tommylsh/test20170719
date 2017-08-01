package com.maxim.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dozer.Mapper;

public class BeanMapper {

    private Mapper mapper;

    public BeanMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public <T> T map(Object source, Class<T> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    public void map(Object source, Object destination) {
        mapper.map(source, destination);
    }

    public <T> T map(Object source, Class<T> destinationClass, String mapId) {
        return mapper.map(source, destinationClass, mapId);
    }

    public void map(Object source, Object destination, String mapId) {
        mapper.map(source, destination, mapId);
    }
    
    public <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        List<T> destinationList = new ArrayList<T>();
        for (Object sourceObject : sourceList) {
            T destinationObject = mapper.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }
    
    public <T> T mapTo(Object sourceObject, Class<T> destinationClass) {
            T destinationObject = mapper.map(sourceObject, destinationClass);
        return destinationObject;
    }

}
