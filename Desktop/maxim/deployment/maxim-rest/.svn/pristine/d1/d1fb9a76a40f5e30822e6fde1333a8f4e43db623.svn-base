package com.maxim.core.service;

import com.maxim.api.model.Orders;
import com.maxim.api.service.OrdersService;
import com.maxim.common.enums.ProcessState;
import com.maxim.common.exception.DatabaseException;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;
import com.maxim.core.mapper.OrdersMapper;
import com.maxim.core.mapper.ext.OrdersExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service("ordersService")
public class OrdersServiceImpl implements OrdersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Resource(name = "ordersMapper")
    private OrdersMapper ordersMapper;

    @Resource(name = "ordersExtMapper")
    private OrdersExtMapper ordersExtMapper;

    @Override
    public int add(Orders orders) {
        if (orders == null) {
            throw new ValidationException("[Validation failed] - this argument [orders] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateObject(orders, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            orders.setStatus(ProcessState.COMPLETED.getValue());
            return ordersMapper.insertSelective(orders);
        } catch (Exception e) {
            LOGGER.error("Insert Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public int add(List<Orders> ordersList) {
        if (ordersList == null || ordersList.isEmpty()) {
            throw new ValidationException("[Validation failed] - this argument [ordersList] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateCollection(ordersList, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            ordersExtMapper.deleteBatch(ordersList);
            return ordersExtMapper.insertBatch(ordersList);
        } catch (Exception e) {
            LOGGER.error("Batch Insert Or Delete Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

}
