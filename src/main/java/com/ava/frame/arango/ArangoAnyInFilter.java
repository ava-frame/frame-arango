package com.ava.frame.arango;

/**
 * Created by redredava on 2018/7/23.
 * email:zhyx2014@yeah.net
 */
public class ArangoAnyInFilter extends ArangoFilter {
    private String[] inFieldArray;

    public String[] getInFieldArray() {
        return inFieldArray;
    }

    public void setInFieldArray(String[] inFieldArray) {
        this.inFieldArray = inFieldArray;
    }

    public ArangoAnyInFilter(String field, Object value, String[] inFieldArray) {
        super(field, value);
        this.inFieldArray = inFieldArray;
    }
}
