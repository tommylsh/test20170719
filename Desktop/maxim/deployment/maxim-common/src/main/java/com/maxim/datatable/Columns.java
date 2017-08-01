package com.maxim.datatable;

public class Columns {

    private String data;
    private String name;
    private boolean serachable = false;
    private boolean orderable = false;
    private Search search;

    public static final class Search {
        private String value;
        private boolean regex = false;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isRegex() {
            return regex;
        }

        public void setRegex(boolean regex) {
            this.regex = regex;
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSerachable() {
        return serachable;
    }

    public void setSerachable(boolean serachable) {
        this.serachable = serachable;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

}
