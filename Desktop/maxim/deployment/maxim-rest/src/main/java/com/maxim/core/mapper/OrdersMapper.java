package com.maxim.core.mapper;

import com.maxim.api.model.Orders;

public interface OrdersMapper {
    int insert(Orders record);

    int insertSelective(Orders record);

    int delete(Orders order);
}