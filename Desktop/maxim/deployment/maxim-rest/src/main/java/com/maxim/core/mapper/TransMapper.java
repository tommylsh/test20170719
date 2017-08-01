package com.maxim.core.mapper;

import com.maxim.api.model.Orders;
import com.maxim.api.model.Trans;

public interface TransMapper {
    int insert(Trans record);

    int insertSelective(Trans record);

    int delete(Orders record);
}