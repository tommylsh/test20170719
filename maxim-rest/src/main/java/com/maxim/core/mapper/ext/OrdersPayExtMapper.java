package com.maxim.core.mapper.ext;

import com.maxim.api.model.OrdersPay;

import java.util.List;

public interface OrdersPayExtMapper {

    int insertBatch(List<OrdersPay> records);

    int deleteBatch(List<OrdersPay> records);

}