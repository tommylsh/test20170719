package com.maxim.core.mapper;

import com.maxim.api.model.OrdersExtra;

public interface OrdersExtraMapper {
    int insert(OrdersExtra record);

    int insertSelective(OrdersExtra record);
}