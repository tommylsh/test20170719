package com.maxim.api.model;

import com.maxim.common.model.AbstractModel;
import com.maxim.common.validation.Group;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Trans extends AbstractModel implements Serializable {
    @NotEmpty(groups = {Group.Add.class})
    @Length(groups = {Group.Add.class}, max = 50)
    private String rowguid;

    @NotEmpty(groups = {Group.Add.class})
    private String branchCode;

    @NotNull(groups = {Group.Add.class})
    private Date businessDate;

    @NotEmpty(groups = {Group.Add.class})
    private String orderNo;

    private String eventNo;

    @NotEmpty(groups = {Group.Add.class})
    private String transType;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal orderSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal itemSeq;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal subitemSeq;

    @NotNull(groups = {Group.Add.class})
    private Date transDatetime;

    private BigDecimal uid;

    private String stationId;

    @NotEmpty(groups = {Group.Add.class})
    private String deptCode;

    @NotNull(groups = {Group.Add.class})
    private Object deptCdesc;

    @NotNull(groups = {Group.Add.class})
    private Object deptEdesc;
    @NotEmpty(groups = {Group.Add.class})
    private String itemCode;

    @NotNull(groups = {Group.Add.class})
    private Object itemCdesc;

    @NotNull(groups = {Group.Add.class})
    private Object itemEdesc;

    @NotNull(groups = {Group.Add.class})
    private Object prnCdesc;

    @NotNull(groups = {Group.Add.class})
    private Object prnEdesc;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal unitPrice;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal itemQty;

    @NotEmpty(groups = {Group.Add.class})
    private String unitMeasure;

    @NotEmpty(groups = {Group.Add.class})
    private String unitBase;

    private String nCakeSale;

    private String discProd;

    @NotEmpty(groups = {Group.Add.class})
    private String takePlace;

    private BigDecimal serviceChg;

    private String discServChg;

    private BigDecimal gstRate;

    private String pmtNo;

    private BigDecimal discOffRate;

    private BigDecimal deductAmt;

    private String freeItem;

    private BigDecimal modifAmt;

    @NotNull(groups = {Group.Add.class})
    private BigDecimal subTotal;

    private String setMenu;

    private String setMenuCode;

    private BigDecimal checkDiscAlloc;

    private BigDecimal payDiscAlloc;

    private BigDecimal recall;

    private String refund;

    private String voidVal;

    private Date voidDatetime;

    private BigDecimal voidUid;

    private String voidStationId;

    private BigDecimal authUid;

    private String voidReason;

    private Date postDatetime;

    private String reserved;

    private String status;

    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

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

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Object getDeptCdesc() {
        return deptCdesc;
    }

    public void setDeptCdesc(Object deptCdesc) {
        this.deptCdesc = deptCdesc;
    }

    public Object getDeptEdesc() {
        return deptEdesc;
    }

    public void setDeptEdesc(Object deptEdesc) {
        this.deptEdesc = deptEdesc;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Object getItemCdesc() {
        return itemCdesc;
    }

    public void setItemCdesc(Object itemCdesc) {
        this.itemCdesc = itemCdesc;
    }

    public Object getItemEdesc() {
        return itemEdesc;
    }

    public void setItemEdesc(Object itemEdesc) {
        this.itemEdesc = itemEdesc;
    }

    public Object getPrnCdesc() {
        return prnCdesc;
    }

    public void setPrnCdesc(Object prnCdesc) {
        this.prnCdesc = prnCdesc;
    }

    public Object getPrnEdesc() {
        return prnEdesc;
    }

    public void setPrnEdesc(Object prnEdesc) {
        this.prnEdesc = prnEdesc;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getItemQty() {
        return itemQty;
    }

    public void setItemQty(BigDecimal itemQty) {
        this.itemQty = itemQty;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getUnitBase() {
        return unitBase;
    }

    public void setUnitBase(String unitBase) {
        this.unitBase = unitBase;
    }

    public String getnCakeSale() {
        return nCakeSale;
    }

    public void setnCakeSale(String nCakeSale) {
        this.nCakeSale = nCakeSale;
    }

    public String getDiscProd() {
        return discProd;
    }

    public void setDiscProd(String discProd) {
        this.discProd = discProd;
    }

    public String getTakePlace() {
        return takePlace;
    }

    public void setTakePlace(String takePlace) {
        this.takePlace = takePlace;
    }

    public BigDecimal getServiceChg() {
        return serviceChg;
    }

    public void setServiceChg(BigDecimal serviceChg) {
        this.serviceChg = serviceChg;
    }

    public String getDiscServChg() {
        return discServChg;
    }

    public void setDiscServChg(String discServChg) {
        this.discServChg = discServChg;
    }

    public BigDecimal getGstRate() {
        return gstRate;
    }

    public void setGstRate(BigDecimal gstRate) {
        this.gstRate = gstRate;
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

    public String getFreeItem() {
        return freeItem;
    }

    public void setFreeItem(String freeItem) {
        this.freeItem = freeItem;
    }

    public BigDecimal getModifAmt() {
        return modifAmt;
    }

    public void setModifAmt(BigDecimal modifAmt) {
        this.modifAmt = modifAmt;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getSetMenu() {
        return setMenu;
    }

    public void setSetMenu(String setMenu) {
        this.setMenu = setMenu;
    }

    public String getSetMenuCode() {
        return setMenuCode;
    }

    public void setSetMenuCode(String setMenuCode) {
        this.setMenuCode = setMenuCode;
    }

    public BigDecimal getCheckDiscAlloc() {
        return checkDiscAlloc;
    }

    public void setCheckDiscAlloc(BigDecimal checkDiscAlloc) {
        this.checkDiscAlloc = checkDiscAlloc;
    }

    public BigDecimal getPayDiscAlloc() {
        return payDiscAlloc;
    }

    public void setPayDiscAlloc(BigDecimal payDiscAlloc) {
        this.payDiscAlloc = payDiscAlloc;
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

    public Date getPostDatetime() {
        return postDatetime;
    }

    public void setPostDatetime(Date postDatetime) {
        this.postDatetime = postDatetime;
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
        sb.append(", itemSeq=").append(itemSeq);
        sb.append(", subitemSeq=").append(subitemSeq);
        sb.append(", transDatetime=").append(transDatetime);
        sb.append(", uid=").append(uid);
        sb.append(", stationId=").append(stationId);
        sb.append(", deptCode=").append(deptCode);
        sb.append(", deptCdesc=").append(deptCdesc);
        sb.append(", deptEdesc=").append(deptEdesc);
        sb.append(", itemCode=").append(itemCode);
        sb.append(", itemCdesc=").append(itemCdesc);
        sb.append(", itemEdesc=").append(itemEdesc);
        sb.append(", prnCdesc=").append(prnCdesc);
        sb.append(", prnEdesc=").append(prnEdesc);
        sb.append(", unitPrice=").append(unitPrice);
        sb.append(", itemQty=").append(itemQty);
        sb.append(", unitMeasure=").append(unitMeasure);
        sb.append(", unitBase=").append(unitBase);
        sb.append(", nCakeSale=").append(nCakeSale);
        sb.append(", discProd=").append(discProd);
        sb.append(", takePlace=").append(takePlace);
        sb.append(", serviceChg=").append(serviceChg);
        sb.append(", discServChg=").append(discServChg);
        sb.append(", gstRate=").append(gstRate);
        sb.append(", pmtNo=").append(pmtNo);
        sb.append(", discOffRate=").append(discOffRate);
        sb.append(", deductAmt=").append(deductAmt);
        sb.append(", freeItem=").append(freeItem);
        sb.append(", modifAmt=").append(modifAmt);
        sb.append(", subTotal=").append(subTotal);
        sb.append(", setMenu=").append(setMenu);
        sb.append(", setMenuCode=").append(setMenuCode);
        sb.append(", checkDiscAlloc=").append(checkDiscAlloc);
        sb.append(", payDiscAlloc=").append(payDiscAlloc);
        sb.append(", recall=").append(recall);
        sb.append(", refund=").append(refund);
        sb.append(", voidVal=").append(voidVal);
        sb.append(", voidDatetime=").append(voidDatetime);
        sb.append(", voidUid=").append(voidUid);
        sb.append(", voidStationId=").append(voidStationId);
        sb.append(", authUid=").append(authUid);
        sb.append(", voidReason=").append(voidReason);
        sb.append(", postDatetime=").append(postDatetime);
        sb.append(", reserved=").append(reserved);
        sb.append(", status=").append(status);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}