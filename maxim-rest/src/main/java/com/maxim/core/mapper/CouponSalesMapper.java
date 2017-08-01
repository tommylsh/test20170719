package com.maxim.core.mapper;

import com.maxim.api.model.CouponSales;

public interface CouponSalesMapper {
    int insert(CouponSales record);

    int insertSelective(CouponSales record);
}