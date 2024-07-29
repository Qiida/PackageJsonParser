package com.iav.et.data.expectation;

public class Expectation {
    final private String value;

    public Expectation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "value == " + value;
    }
}
