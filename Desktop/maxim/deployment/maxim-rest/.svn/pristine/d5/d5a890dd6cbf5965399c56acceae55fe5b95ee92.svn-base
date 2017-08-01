package com.maxim.core.service;

import com.maxim.api.model.OrdersExtra;
import com.maxim.api.service.OrdersExtraService;
import com.maxim.common.enums.ProcessState;
import com.maxim.common.exception.DatabaseException;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;
import com.maxim.core.mapper.OrdersExtraMapper;
import com.maxim.core.mapper.ext.OrdersExtraExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service("ordersExtraService")
public class OrdersExtraServiceImpl implements OrdersExtraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersExtraServiceImpl.class);

    @Resource(name = "ordersExtraMapper")
    private OrdersExtraMapper ordersExtraMapper;

    @Resource(name = "ordersExtraExtMapper")
    private OrdersExtraExtMapper ordersExtraExtMapper;

    @Override
    public int add(OrdersExtra ordersExtra) {
        if (ordersExtra == null) {
            throw new ValidationException("[Validation failed] - this argument [ordersExtra] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateObject(ordersExtra, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            ordersExtra.setStatus(ProcessState.COMPLETED.getValue());
            return ordersExtraMapper.insertSelective(ordersExtra);
        } catch (Exception e) {
            LOGGER.error("Insert Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public int add(List<OrdersExtra> ordersExtraList) {
        if (ordersExtraList == null || ordersExtraList.isEmpty()) {
            throw new ValidationException("[Validation failed] - this argument [ordersExtraList] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateCollection(ordersExtraList, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            ordersExtraExtMapper.deleteBatch(ordersExtraList);
            return ordersExtraExtMapper.insertBatch(ordersExtraList);
        } catch (Exception e) {
            LOGGER.error("Batch Insert Or Delete Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

}
