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
public class OrdersPay extends AbstractModel implements Serializable {

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

    @NotNull(groups = {Group.Add.class})
    private BigDecimal paySeq;

    @NotNull(groups = {Group.Add.class})
    private Date transDatetime;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal uid;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 4)
    private String stationId;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 10)
    private String payCode;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String payCdesc;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String payEdesc;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String prnCdesc;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String prnEdesc;

    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 3)
    private String curyNo;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal tender;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal exchRate;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal payQty;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal payAmt;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal tips;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal change;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String cardType;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String subCardType;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 3)
    private String entryMode;

  //  @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String cardNo;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 40)
    private String cardHolderName;

    //@NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String requestCode;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String responseCode;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String batchNo;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String traceNo;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String approvalCode;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal prvVal;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal newVal;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal curyPrvVal;

  //  @NotNull(groups = {Group.Add.class})
    private BigDecimal curyNewVal;

   // @NotNull(groups = {Group.Add.class})
    private BigDecimal recall;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String refund;

    @XmlElement(name = "void")
   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 1)
    private String voidVal;

    //@NotNull(groups = {Group.Add.class})
    private BigDecimal pending;

   // @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 20)
    private String reserved;

    // @NotEmpty(groups = {Group.Add.class})
    // @Length(groups = {Group.Add.class}, max = 2)
    private String status;

   // @NotNull(groups = {Group.Add.class})
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

    public BigDecimal getPaySeq() {
        return paySeq;
    }

    public void setPaySeq(BigDecimal paySeq) {
        this.paySeq = paySeq;
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

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPayCdesc() {
        return payCdesc;
    }

    public void setPayCdesc(String payCdesc) {
        this.payCdesc = payCdesc;
    }

    public String getPayEdesc() {
        return payEdesc;
    }

    public void setPayEdesc(String payEdesc) {
        this.payEdesc = payEdesc;
    }

    public String getPrnCdesc() {
        return prnCdesc;
    }

    public void setPrnCdesc(String prnCdesc) {
        this.prnCdesc = prnCdesc;
    }

    public String getPrnEdesc() {
        return prnEdesc;
    }

    public void setPrnEdesc(String prnEdesc) {
        this.prnEdesc = prnEdesc;
    }

    public String getCuryNo() {
        return curyNo;
    }

    public void setCuryNo(String curyNo) {
        this.curyNo = curyNo;
    }

    public BigDecimal getTender() {
        return tender;
    }

    public void setTender(BigDecimal tender) {
        this.tender = tender;
    }

    public BigDecimal getExchRate() {
        return exchRate;
    }

    public void setExchRate(BigDecimal exchRate) {
        this.exchRate = exchRate;
    }

    public BigDecimal getPayQty() {
        return payQty;
    }

    public void setPayQty(BigDecimal payQty) {
        this.payQty = payQty;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getTips() {
        return tips;
    }

    public void setTips(BigDecimal tips) {
        this.tips = tips;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getSubCardType() {
        return subCardType;
    }

    public void setSubCardType(String subCardType) {
        this.subCardType = subCardType;
    }

    public String getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public BigDecimal getPrvVal() {
        return prvVal;
    }

    public void setPrvVal(BigDecimal prvVal) {
        this.prvVal = prvVal;
    }

    public BigDecimal getNewVal() {
        return newVal;
    }

    public void setNewVal(BigDecimal newVal) {
        this.newVal = newVal;
    }

    public BigDecimal getCuryPrvVal() {
        return curyPrvVal;
    }

    public void setCuryPrvVal(BigDecimal curyPrvVal) {
        this.curyPrvVal = curyPrvVal;
    }

    public BigDecimal getCuryNewVal() {
        return curyNewVal;
    }

    public void setCuryNewVal(BigDecimal curyNewVal) {
        this.curyNewVal = curyNewVal;
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
        sb.append(", paySeq=").append(paySeq);
        sb.append(", transDatetime=").append(transDatetime);
        sb.append(", uid=").append(uid);
        sb.append(", stationId=").append(stationId);
        sb.append(", payCode=").append(payCode);
        sb.append(", payCdesc=").append(payCdesc);
        sb.append(", payEdesc=").append(payEdesc);
        sb.append(", prnCdesc=").append(prnCdesc);
        sb.append(", prnEdesc=").append(prnEdesc);
        sb.append(", curyNo=").append(curyNo);
        sb.append(", tender=").append(tender);
        sb.append(", exchRate=").append(exchRate);
        sb.append(", payQty=").append(payQty);
        sb.append(", payAmt=").append(payAmt);
        sb.append(", tips=").append(tips);
        sb.append(", change=").append(change);
        sb.append(", cardType=").append(cardType);
        sb.append(", subCardType=").append(subCardType);
        sb.append(", entryMode=").append(entryMode);
        sb.append(", cardNo=").append(cardNo);
        sb.append(", cardHolderName=").append(cardHolderName);
        sb.append(", requestCode=").append(requestCode);
        sb.append(", responseCode=").append(responseCode);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", traceNo=").append(traceNo);
        sb.append(", approvalCode=").append(approvalCode);
        sb.append(", prvVal=").append(prvVal);
        sb.append(", newVal=").append(newVal);
        sb.append(", curyPrvVal=").append(curyPrvVal);
        sb.append(", curyNewVal=").append(curyNewVal);
        sb.append(", recall=").append(recall);
        sb.append(", refund=").append(refund);
        sb.append(", voidVal=").append(voidVal);
        sb.append(", pending=").append(pending);
        sb.append(", reserved=").append(reserved);
        sb.append(", status=").append(status);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}