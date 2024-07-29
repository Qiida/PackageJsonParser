package com.iav.et.data.expectation;

public class StringListExpectation extends Expectation {

    final String[] strings;
    public StringListExpectation(String[] strings) {
        super("[" + String.join(", ", strings) + "]");
        this.strings = strings;
    }

    public String[] getStrings() {
        return strings;
    }
}
