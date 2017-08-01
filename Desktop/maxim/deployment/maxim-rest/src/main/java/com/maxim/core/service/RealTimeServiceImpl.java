package com.maxim.core.service;

import java.util.List;

import javax.annotation.Resource;

import com.maxim.api.service.*;
import com.maxim.core.mapper.CouponSalesMapper;
import com.maxim.core.mapper.OrdersMapper;
import com.maxim.core.mapper.OrdersPayMapper;
import com.maxim.core.mapper.TransMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.api.model.CouponSales;
import com.maxim.api.model.Orders;
import com.maxim.api.model.OrdersExtra;
import com.maxim.api.model.OrdersPay;
import com.maxim.api.model.RealTimeSalesData;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;

@Transactional
@Service("realTimeService")
public class RealTimeServiceImpl implements RealTimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeServiceImpl.class);

    @Resource(name = "ordersService")
    private OrdersService ordersService;

    @Resource(name = "ordersPayService")
    private OrdersPayService ordersPayService;

//    @Resource(name = "ordersExtraService")
//    private OrdersExtraService ordersExtraService;

    @Resource(name = "couponSalesService")
    private CouponSalesService couponSalesService;

    @Resource(name = "transService")
    private TransService transService;

    @Resource(name = "ordersMapper")
    private OrdersMapper ordersMapper;
    @Resource(name = "ordersPayMapper")
    private OrdersPayMapper ordersPayMapper;
    @Resource(name = "transMapper")
    private TransMapper transMapper;
    @Resource(name = "couponSalesMapper")
    private CouponSalesMapper couponSalesMapper;

    @Override
    public int add(RealTimeSalesData realTimeSalesData) {
        if (realTimeSalesData == null) {
            throw new ValidationException("[Validation failed] - this argument [realTimeSalesData] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateObject(realTimeSalesData, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }

        List<Orders> distinctOrders =  ordersService.getDistinct(realTimeSalesData.getOrdersList());
        List<CouponSales> couponSalesList = realTimeSalesData.getCouponSalesList();
        for (Orders order:
                distinctOrders) {
            ordersMapper.delete(order);
            ordersPayMapper.delete(order);
            transMapper.delete(order);
            if (couponSalesList != null && couponSalesList.size() > 0) {
                couponSalesMapper.delete(order);
            }
        }

        int count = 0;
//      count += ordersExtraService.add(realTimeSalesData.getOrdersExtraList());
        count += ordersService.add(realTimeSalesData.getOrdersList());
        count += ordersPayService.add(realTimeSalesData.getOrdersPayList());
        count += transService.add(realTimeSalesData.getTransList());

        if (couponSalesList != null && couponSalesList.size() > 0) {
            count += couponSalesService.add(couponSalesList);
        }
        LOGGER.info("Successfully insert {} records, request data: {}", count, realTimeSalesData);
        return count;
    }

}
