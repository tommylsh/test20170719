package com.maxim.core.mapper.ext;

import com.maxim.api.model.Orders;

import java.util.List;

public interface OrdersExtMapper {

    int insertBatch(List<Orders> records);

    int deleteBatch(List<Orders> records);

}