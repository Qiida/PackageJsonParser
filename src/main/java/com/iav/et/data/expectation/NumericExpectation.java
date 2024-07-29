package com.iav.et.data.expectation;

public class NumericExpectation extends Expectation {

    final private String relation;
    final private String tolerance;
    final private String toleranceType;
    public NumericExpectation(String value, String relation, String tolerance, String toleranceType) {
        super(value);
        this.relation = relation;
        this.tolerance = tolerance;
        this.toleranceType = toleranceType;
    }

    public String getRelation() {
        return relation;
    }

    public String getTolerance() {
        return tolerance;
    }

    public String getToleranceType() {
        return toleranceType;
    }

    public String toString() {
        String string = "value " + relation + " " + getValue();
        if (tolerance != null) {
            string += "+/-" + tolerance + " (" + toleranceType + ")";
        }
        return string;
    }
}
