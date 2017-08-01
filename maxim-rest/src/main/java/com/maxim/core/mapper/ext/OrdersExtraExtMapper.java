package com.maxim.core.mapper.ext;

import com.maxim.api.model.OrdersExtra;

import java.util.List;

public interface OrdersExtraExtMapper {

    int insertBatch(List<OrdersExtra> records);

    int deleteBatch(List<OrdersExtra> records);

}