package com.maxim.api.service;

import com.maxim.api.model.CouponSales;

import java.util.List;

public interface CouponSalesService {

    int add(CouponSales couponSales);

    int add(List<CouponSales> couponSalesList);

}
