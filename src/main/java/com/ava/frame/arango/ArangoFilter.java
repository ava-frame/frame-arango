package com.ava.frame.arango;

/**
 * Created by redredava on 2018/7/23.
 * email:zhyx2014@yeah.net
 */
public class ArangoFilter {
    private String field;
    private Object value;
    private String filterString;

    public ArangoFilter(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public Object getValue() {
        return value;
    }
}
