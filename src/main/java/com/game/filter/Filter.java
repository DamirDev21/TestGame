package com.game.filter;

public class Filter {
    private final String field;
    private final QueryOperator operator;
    private final String value;

    public Filter(String field, QueryOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public QueryOperator getOperator() {
        return operator;
    }
}
