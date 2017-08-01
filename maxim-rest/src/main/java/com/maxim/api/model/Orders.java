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
public class Orders extends AbstractModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 50)
    private String rowguid;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 6)
    private String branchCode;

    @NotNull(groups = {Group.Add.class})
    private Date businessDate;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String orderNo;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String eventNo;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String transType;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal orderSeq;

    @NotNull(groups = {Group.Add.class})
    private Date transDatetime;

    //@NotNull(groups = {Group.Add.class})
    private BigDecimal uid;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String stationId;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String tableId;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal nop;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal grossAmt;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal totalQty;

  //  @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String pmtNo;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal discOffRate;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal deductAmt;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal netAmt;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal servChgAmt;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String discServChg;

    //@NotNull(groups = {Group.Add.class})
    private BigDecimal gstAmt;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal grandTotal;

  //  @NotNull(groups = {Group.Add.class})
    private BigDecimal deposit;

  //  @NotNull(groups = {Group.Add.class})
    private BigDecimal remain;

    // @NotNull(groups = {Group.Add.class})
    private Date closeDatetime;

    // @NotNull(groups = {Group.Add.class})
    private BigDecimal closeUid;

    // @NotEmpty(groups = {Group.Add.class})
    // @Length(groups = {Group.Add.class}, max = 4)
    private String closeStationId;

    //@NotNull(groups = {Group.Add.class})
    private BigDecimal recall;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String refund;

    @XmlElement(name = "void")
    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String voidVal;

    // @NotNull(groups = {Group.Add.class})
    private Date voidDatetime;

    // @NotNull(groups = {Group.Add.class})
    private BigDecimal voidUid;

    // @NotEmpty(groups = {Group.Add.class})
    // @Length(groups = {Group.Add.class}, max = 4)
    private String voidStationId;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String orgOrderNo;

    // @NotNull(groups = {Group.Add.class})
    private BigDecimal authUid;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String voidReason;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal printTimes;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String remarkType;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String remarks;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal pending;

  //  @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String reserved;

    // @NotEmpty(groups = {Group.Add.class})
    // @Length(groups = {Group.Add.class}, max = 2)
    private String status;

    //@NotNull(groups = {Group.Add.class})
    private Date lastUpdateTime;

    public String getRowguid() {
        return rowguid;
    }

    public void setRowguid(String rowguid) {
        this.rowguid = rowguid;
    }

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

    public BigDecimal getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(BigDecimal orderSeq) {
        this.orderSeq = orderSeq;
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

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public BigDecimal getNop() {
        return nop;
    }

    public void setNop(BigDecimal nop) {
        this.nop = nop;
    }

    public BigDecimal getGrossAmt() {
        return grossAmt;
    }

    public void setGrossAmt(BigDecimal grossAmt) {
        this.grossAmt = grossAmt;
    }

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public String getPmtNo() {
        return pmtNo;
    }

    public void setPmtNo(String pmtNo) {
        this.pmtNo = pmtNo;
    }

    public BigDecimal getDiscOffRate() {
        return discOffRate;
    }

    public void setDiscOffRate(BigDecimal discOffRate) {
        this.discOffRate = discOffRate;
    }

    public BigDecimal getDeductAmt() {
        return deductAmt;
    }

    public void setDeductAmt(BigDecimal deductAmt) {
        this.deductAmt = deductAmt;
    }

    public BigDecimal getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(BigDecimal netAmt) {
        this.netAmt = netAmt;
    }

    public BigDecimal getServChgAmt() {
        return servChgAmt;
    }

    public void setServChgAmt(BigDecimal servChgAmt) {
        this.servChgAmt = servChgAmt;
    }

    public String getDiscServChg() {
        return discServChg;
    }

    public void setDiscServChg(String discServChg) {
        this.discServChg = discServChg;
    }

    public BigDecimal getGstAmt() {
        return gstAmt;
    }

    public void setGstAmt(BigDecimal gstAmt) {
        this.gstAmt = gstAmt;
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

    public Date getCloseDatetime() {
        return closeDatetime;
    }

    public void setCloseDatetime(Date closeDatetime) {
        this.closeDatetime = closeDatetime;
    }

    public BigDecimal getCloseUid() {
        return closeUid;
    }

    public void setCloseUid(BigDecimal closeUid) {
        this.closeUid = closeUid;
    }

    public String getCloseStationId() {
        return closeStationId;
    }

    public void setCloseStationId(String closeStationId) {
        this.closeStationId = closeStationId;
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

    public Date getVoidDatetime() {
        return voidDatetime;
    }

    public void setVoidDatetime(Date voidDatetime) {
        this.voidDatetime = voidDatetime;
    }

    public BigDecimal getVoidUid() {
        return voidUid;
    }

    public void setVoidUid(BigDecimal voidUid) {
        this.voidUid = voidUid;
    }

    public String getVoidStationId() {
        return voidStationId;
    }

    public void setVoidStationId(String voidStationId) {
        this.voidStationId = voidStationId;
    }

    public String getOrgOrderNo() {
        return orgOrderNo;
    }

    public void setOrgOrderNo(String orgOrderNo) {
        this.orgOrderNo = orgOrderNo;
    }

    public BigDecimal getAuthUid() {
        return authUid;
    }

    public void setAuthUid(BigDecimal authUid) {
        this.authUid = authUid;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public BigDecimal getPrintTimes() {
        return printTimes;
    }

    public void setPrintTimes(BigDecimal printTimes) {
        this.printTimes = printTimes;
    }

    public String getRemarkType() {
        return remarkType;
    }

    public void setRemarkType(String remarkType) {
        this.remarkType = remarkType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getPending() {
        return pending;
    }

    public void setPending(BigDecimal pending) {
        this.pending = pending;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", rowguid=").append(rowguid);
        sb.append(", branchCode=").append(branchCode);
        sb.append(", businessDate=").append(businessDate);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", eventNo=").append(eventNo);
        sb.append(", transType=").append(transType);
        sb.append(", orderSeq=").append(orderSeq);
        sb.append(", transDatetime=").append(transDatetime);
        sb.append(", uid=").append(uid);
        sb.append(", stationId=").append(stationId);
        sb.append(", tableId=").append(tableId);
        sb.append(", nop=").append(nop);
        sb.append(", grossAmt=").append(grossAmt);
        sb.append(", totalQty=").append(totalQty);
        sb.append(", pmtNo=").append(pmtNo);
        sb.append(", discOffRate=").append(discOffRate);
        sb.append(", deductAmt=").append(deductAmt);
        sb.append(", netAmt=").append(netAmt);
        sb.append(", servChgAmt=").append(servChgAmt);
        sb.append(", discServChg=").append(discServChg);
        sb.append(", gstAmt=").append(gstAmt);
        sb.append(", grandTotal=").append(grandTotal);
        sb.append(", deposit=").append(deposit);
        sb.append(", remain=").append(remain);
        sb.append(", closeDatetime=").append(closeDatetime);
        sb.append(", closeUid=").append(closeUid);
        sb.append(", closeStationId=").append(closeStationId);
        sb.append(", recall=").append(recall);
        sb.append(", refund=").append(refund);
        sb.append(", voidVal=").append(voidVal);
        sb.append(", voidDatetime=").append(voidDatetime);
        sb.append(", voidUid=").append(voidUid);
        sb.append(", voidStationId=").append(voidStationId);
        sb.append(", orgOrderNo=").append(orgOrderNo);
        sb.append(", authUid=").append(authUid);
        sb.append(", voidReason=").append(voidReason);
        sb.append(", printTimes=").append(printTimes);
        sb.append(", remarkType=").append(remarkType);
        sb.append(", remarks=").append(remarks);
        sb.append(", pending=").append(pending);
        sb.append(", reserved=").append(reserved);
        sb.append(", status=").append(status);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}