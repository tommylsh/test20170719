package com.maxim.api.service;

import com.maxim.api.model.Orders;

import java.util.List;

public interface OrdersService {

    int add(Orders orders);

    int add(List<Orders> ordersList);
    public List<Orders> getDistinct(List<Orders> ordersList);

}
