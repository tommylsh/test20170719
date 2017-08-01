package com.maxim.api.model;

import com.maxim.common.validation.Group;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class RealTimeSalesData {

    @NotNull(groups = {Group.Add.class})
    @Size(groups = {Group.Add.class}, min = 1)
    private List<Orders> ordersList;

    @NotNull(groups = {Group.Add.class})
    @Size(groups = {Group.Add.class}, min = 1)
    private List<OrdersPay> ordersPayList;

//    @NotNull(groups = {Group.Add.class})
//    @Size(groups = {Group.Add.class})
//    private List<OrdersExtra> ordersExtraList;

    @NotNull(groups = {Group.Add.class})
    @Size(groups = {Group.Add.class})
    private List<Trans> transList;

    private List<CouponSales> couponSalesList;

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    public List<OrdersPay> getOrdersPayList() {
        return ordersPayList;
    }

    public void setOrdersPayList(List<OrdersPay> ordersPayList) {
        this.ordersPayList = ordersPayList;
    }

//    public List<OrdersExtra> getOrdersExtraList() {
//        return ordersExtraList;
//    }
//
//    public void setOrdersExtraList(List<OrdersExtra> ordersExtraList) {
//        this.ordersExtraList = ordersExtraList;
//    }


    public List<Trans> getTransList() {
        return transList;
    }

    public void setTransList(List<Trans> transList) {
        this.transList = transList;
    }

    public List<CouponSales> getCouponSalesList() {
        return couponSalesList;
    }

    public void setCouponSalesList(List<CouponSales> couponSalesList) {
        this.couponSalesList = couponSalesList;
    }

    @Override
    public String toString() {
        return "RealTimeSalesData{" +
                "ordersList=" + ordersList +
                ", ordersPayList=" + ordersPayList +
                ", couponSalesList=" + couponSalesList +
                '}';
    }
}
