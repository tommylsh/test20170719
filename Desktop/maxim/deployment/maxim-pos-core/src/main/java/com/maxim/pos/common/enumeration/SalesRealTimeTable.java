package com.maxim.pos.common.enumeration;

/**
 * Sales Real Time Table(EDW)
 */
public enum SalesRealTimeTable {

    ORDERS("ORDERS"),
    ORDERS_PAY("ORDERS_PAY"),
    ORDERS_EXTRA("ORDERS_EXTRA"),
    TRANS("TRANS"),
    COUPON_SALES("COUPON_SALES");

    private final String tableName;

    SalesRealTimeTable(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public static SalesRealTimeTable fromTableName(final String tableName) {
        for (SalesRealTimeTable salesRealTimeTable : SalesRealTimeTable.values()) {
            if (salesRealTimeTable.tableName.equalsIgnoreCase(tableName)) {
                return salesRealTimeTable;
            }
        }
        return null;
    }

}