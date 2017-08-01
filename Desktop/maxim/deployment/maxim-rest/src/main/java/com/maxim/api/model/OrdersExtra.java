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
public class OrdersExtra extends AbstractModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 6)
    private String branchCode;

    @NotNull(groups = {Group.Add.class})
    private Date businessDate;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String orderNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String eventNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String transType;

    @NotNull(groups = {Group.Add.class})
    private Integer orderSeq;

    @NotNull(groups = {Group.Add.class})
    private Date transDatetime;

    @NotNull(groups = {Group.Add.class})
    private Long uid;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String stationId;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String contactNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String refNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String delivery;

    @NotNull(groups = {Group.Add.class})
    private Date pickupDatetime;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 6)
    private String pickupBranch;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String trip;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 6)
    private String productionBranch;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal grossAmt;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String postDiscount;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal grandTotal;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal deposit;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal remain;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 50)
    private String remarks;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal recall;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String refund;

    @XmlElement(name = "void")
    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String voidVal;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String reserved;

    private String status;

    private Date lastUpdateTime;

    private String rowguid;

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Date getTransDatetime() {
        return transDatetime;
    }

    public void setTransDatetime(Date transDatetime) {
        this.transDatetime = transDatetime;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public Date getPickupDatetime() {
        return pickupDatetime;
    }

    public void setPickupDatetime(Date pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }

    public String getPickupBranch() {
        return pickupBranch;
    }

    public void setPickupBranch(String pickupBranch) {
        this.pickupBranch = pickupBranch;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getProductionBranch() {
        return productionBranch;
    }

    public void setProductionBranch(String productionBranch) {
        this.productionBranch = productionBranch;
    }

    public BigDecimal getGrossAmt() {
        return grossAmt;
    }

    public void setGrossAmt(BigDecimal grossAmt) {
        this.grossAmt = grossAmt;
    }

    public String getPostDiscount() {
        return postDiscount;
    }

    public void setPostDiscount(String postDiscount) {
        this.postDiscount = postDiscount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getRemain() {
        return remain;
    }

    public void setRemain(BigDecimal remain) {
        this.remain = remain;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getRecall() {
        return recall;
    }

    public void setRecall(BigDecimal recall) {
        this.recall = recall;
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
        sb.append(", businessDate=").append(businessDate);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", eventNo=").append(eventNo);
        sb.append(", transType=").append(transType);
        sb.append(", orderSeq=").append(orderSeq);
        sb.append(", transDatetime=").append(transDatetime);
        sb.append(", uid=").append(uid);
        sb.append(", stationId=").append(stationId);
        sb.append(", contactNo=").append(contactNo);
        sb.append(", refNo=").append(refNo);
        sb.append(", delivery=").append(delivery);
        sb.append(", pickupDatetime=").append(pickupDatetime);
        sb.append(", pickupBranch=").append(pickupBranch);
        sb.append(", trip=").append(trip);
        sb.append(", productionBranch=").append(productionBranch);
        sb.append(", grossAmt=").append(grossAmt);
        sb.append(", postDiscount=").append(postDiscount);
        sb.append(", grandTotal=").append(grandTotal);
        sb.append(", deposit=").append(deposit);
        sb.append(", remain=").append(remain);
        sb.append(", remarks=").append(remarks);
        sb.append(", recall=").append(recall);
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