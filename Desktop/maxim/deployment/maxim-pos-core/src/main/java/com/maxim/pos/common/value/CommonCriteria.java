package com.maxim.pos.common.value;

import java.io.Serializable;

public class CommonCriteria implements Serializable {

    private static final long serialVersionUID = 6781118159188449602L;

    private Long entityId;
    private String keyword;
    private String userId;
    private String percentChar = "%";
    private Boolean notAdminUser;
    private Boolean queryRecord = true;

    private int startFrom;
    private int maxResult;

    public CommonCriteria() {
    }

    public CommonCriteria(Long entityId) {
        this.entityId = entityId;
    }

    public CommonCriteria(Long entityId, String keyword, int startFrom, int maxResult) {
        this.entityId = entityId;
        this.keyword = keyword;
        this.startFrom = startFrom;
        this.maxResult = maxResult;
    }

    protected String emptyToNull(String value) {
        if (isEmpty(value)) {
            return null;
        } else {
            return value;
        }
    }

    protected boolean isEmpty(String value) {
        return value == null || "".equals(value.trim());
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = emptyToNull(keyword);
    }

    public int getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(int startFrom) {
        this.startFrom = startFrom;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = emptyToNull(userId);
    }

    public String getPercentChar() {
        return percentChar;
    }

    public Boolean getNotAdminUser() {
        return notAdminUser;
    }

    public void setNotAdminUser(Boolean notAdminUser) {
        this.notAdminUser = notAdminUser;
    }

    public Boolean getQueryRecord() {
        return queryRecord;
    }

    public void setQueryRecord(Boolean queryRecord) {
        this.queryRecord = queryRecord;
    }

}
