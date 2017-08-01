package com.maxim.core.service;

import com.maxim.api.model.CouponSales;
import com.maxim.api.service.CouponSalesService;
import com.maxim.common.enums.ProcessState;
import com.maxim.common.exception.DatabaseException;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;
import com.maxim.core.mapper.CouponSalesMapper;
import com.maxim.core.mapper.ext.CouponSalesExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service("couponSalesService")
public class CouponSalesServiceImpl implements CouponSalesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouponSalesServiceImpl.class);

    @Resource(name = "couponSalesMapper")
    private CouponSalesMapper couponSalesMapper;

    @Resource(name = "couponSalesExtMapper")
    private CouponSalesExtMapper couponSalesExtMapper;

    @Override
    public int add(CouponSales couponSales) {
        if (couponSales == null) {
            throw new ValidationException("[Validation failed] - this argument [couponSales] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateObject(couponSales, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
            couponSales.setStatus(ProcessState.COMPLETED.getValue());
            return couponSalesMapper.insertSelective(couponSales);
        } catch (Exception e) {
            LOGGER.error("Insert Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public int add(List<CouponSales> couponSalesList) {
        if (couponSalesList == null || couponSalesList.isEmpty()) {
            throw new ValidationException("[Validation failed] - this argument [couponSalesList] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateCollection(couponSalesList, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
//            couponSalesExtMapper.deleteBatch(couponSalesList);
            return couponSalesExtMapper.insertBatch(couponSalesList);
        } catch (Exception e) {
            LOGGER.error("Batch Insert Or Delete Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }

}
