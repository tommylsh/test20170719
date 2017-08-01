package com.maxim.pos.common.data;

public class MasterSyncType {

    private String type;

    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum Type {BRANCH_CODE, BRANCH_TYPE, ALL}

    public static Type fromType(final String type) {
        for (Type t : Type.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }

}
