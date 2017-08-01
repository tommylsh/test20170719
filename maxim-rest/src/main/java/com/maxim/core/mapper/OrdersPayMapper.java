package com.maxim.core.mapper;

import com.maxim.api.model.OrdersPay;

public interface OrdersPayMapper {
    int insert(OrdersPay record);

    int insertSelective(OrdersPay record);
}