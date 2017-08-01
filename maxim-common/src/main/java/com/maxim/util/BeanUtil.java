package com.maxim.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;

import com.maxim.data.CollectionDTO;
import com.maxim.data.DTO;

public class BeanUtil {

    public static Map<String, Object> transBeanToMap(Object bean) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            if (bean == null) {
                return map;
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!"class".equals(key)) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(bean);
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static CollectionDTO transMapToBean(Collection<Map<String, Object>> collection, Class<? extends DTO> clazz) {
        DozerBeanMapper mapper = new DozerBeanMapper();
        
        List<DTO> list = new ArrayList<DTO>();
        Iterator<Map<String, Object>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            list.add(mapper.map(iterator.next(), clazz));
        }
        return new CollectionDTO(list);
    }

}