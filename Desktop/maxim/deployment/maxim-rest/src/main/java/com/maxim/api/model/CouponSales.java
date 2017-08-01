package com.maxim.api.model;

import com.maxim.common.model.AbstractModel;
import com.maxim.common.validation.Group;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CouponSales extends AbstractModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 6)
    private String branchCode;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String orderNo;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal orderSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal itemSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal subitemSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal couponSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal recall;

    @NotNull(groups = {Group.Add.class})
    private Date businessDate;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String eventNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String transType;

    @NotNull(groups = {Group.Add.class})
    private Date transDatetime;

    //@NotNull(groups = {Group.Add.class})
    private BigDecimal uid;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String stationId;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String couponCode;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String couponCdesc;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String couponEdesc;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String rangeRemarks;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String couponStartNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String couponEndNo;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal couponQty;

  //  @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String refund;

    @XmlElement(name = "void")
    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String voidVal;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String reserved;

    // @NotEmpty(groups = {Group.Add.class})
    // @Length(groups = {Group.Add.class}, max = 2)
    private String status;

    //@NotNull(groups = {Group.Add.class})
    private Date lastUpdateTime;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 36)
    private String rowguid;

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(BigDecimal orderSeq) {
        this.orderSeq = orderSeq;
    }

    public BigDecimal getItemSeq() {
        return itemSeq;
    }

    public void setItemSeq(BigDecimal itemSeq) {
        this.itemSeq = itemSeq;
    }

    public BigDecimal getSubitemSeq() {
        return subitemSeq;
    }

    public void setSubitemSeq(BigDecimal subitemSeq) {
        this.subitemSeq = subitemSeq;
    }

    public BigDecimal getCouponSeq() {
        return couponSeq;
    }

    public void setCouponSeq(BigDecimal couponSeq) {
        this.couponSeq = couponSeq;
    }

    public BigDecimal getRecall() {
        return recall;
    }

    public void setRecall(BigDecimal recall) {
        this.recall = recall;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Date getTransDatetime() {
        return transDatetime;
    }

    public void setTransDatetime(Date transDatetime) {
        this.transDatetime = transDatetime;
    }

    public BigDecimal getUid() {
        return uid;
    }

    public void setUid(BigDecimal uid) {
        this.uid = uid;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponCdesc() {
        return couponCdesc;
    }

    public void setCouponCdesc(String couponCdesc) {
        this.couponCdesc = couponCdesc;
    }

    public String getCouponEdesc() {
        return couponEdesc;
    }

    public void setCouponEdesc(String couponEdesc) {
        this.couponEdesc = couponEdesc;
    }

    public String getRangeRemarks() {
        return rangeRemarks;
    }

    public void setRangeRemarks(String rangeRemarks) {
        this.rangeRemarks = rangeRemarks;
    }

    public String getCouponStartNo() {
        return couponStartNo;
    }

    public void setCouponStartNo(String couponStartNo) {
        this.couponStartNo = couponStartNo;
    }

    public String getCouponEndNo() {
        return couponEndNo;
    }

    public void setCouponEndNo(String couponEndNo) {
        this.couponEndNo = couponEndNo;
    }

    public BigDecimal getCouponQty() {
        return couponQty;
    }

    public void setCouponQty(BigDecimal couponQty) {
        this.couponQty = couponQty;
    }

    public String getRefund() {
        return refund;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }

    public String getVoidVal() {
        return voidVal;
    }

    public void setVoidVal(String voidVal) {
        this.voidVal = voidVal;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", branchCode=").append(branchCode);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", orderSeq=").append(orderSeq);
        sb.append(", itemSeq=").append(itemSeq);
        sb.append(", subitemSeq=").append(subitemSeq);
        sb.append(", couponSeq=").append(couponSeq);
        sb.append(", recall=").append(recall);
        sb.append(", businessDate=").append(businessDate);
        sb.append(", eventNo=").append(eventNo);
        sb.append(", transType=").append(transType);
        sb.append(", transDatetime=").append(transDatetime);
        sb.append(", uid=").append(uid);
        sb.append(", stationId=").append(stationId);
        sb.append(", couponCode=").append(couponCode);
        sb.append(", couponCdesc=").append(couponCdesc);
        sb.append(", couponEdesc=").append(couponEdesc);
        sb.append(", rangeRemarks=").append(rangeRemarks);
        sb.append(", couponStartNo=").append(couponStartNo);
        sb.append(", couponEndNo=").append(couponEndNo);
        sb.append(", couponQty=").append(couponQty);
        sb.append(", refund=").append(refund);
        sb.append(", voidVal=").append(voidVal);
        sb.append(", reserved=").append(reserved);
        sb.append(", status=").append(status);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", rowguid=").append(rowguid);
        sb.append("]");
        return sb.toString();
    }
}