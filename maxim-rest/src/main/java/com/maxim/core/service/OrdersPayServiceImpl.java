package com.maxim.core.service;

import com.maxim.api.model.OrdersPay;
import com.maxim.api.service.OrdersPayService;
import com.maxim.common.enums.ProcessState;
import com.maxim.common.exception.DatabaseException;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;
import com.maxim.core.mapper.OrdersPayMapper;
import com.maxim.core.mapper.ext.OrdersPayExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service("ordersPayService")
public class OrdersPayServiceImpl implements OrdersPayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersPayServiceImpl.class);

    @Resource(name = "ordersPayMapper")
    private OrdersPayMapper ordersPayMapper;

    @Resource(name = "ordersPayExtMapper")
    private OrdersPayExtMapper ordersPayExtMapper;

    @Override
    public int add(OrdersPay ordersPay) {
        if (ordersPay == null) {
            throw new ValidationException("[Validation failed] - this argument [ordersPay] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateObject(ordersPay, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            ordersPay.setStatus(ProcessState.COMPLETED.getValue());
            return ordersPayMapper.insertSelective(ordersPay);
        } catch (Exception e) {
            LOGGER.error("Insert Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public int add(List<OrdersPay> ordersPayList) {
        if (ordersPayList == null || ordersPayList.isEmpty()) {
            throw new ValidationException("[Validation failed] - this argument [ordersPayList] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateCollection(ordersPayList, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            ordersPayExtMapper.deleteBatch(ordersPayList);
            return ordersPayExtMapper.insertBatch(ordersPayList);
        } catch (Exception e) {
            LOGGER.error("Batch Insert Or Delete Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

}
