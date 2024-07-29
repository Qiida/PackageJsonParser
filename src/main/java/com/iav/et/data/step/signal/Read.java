package com.iav.et.data.step.signal;

import com.google.gson.JsonObject;
import com.iav.et.data.expectation.Expectation;
import com.iav.et.data.signal.Signal;
import com.iav.et.data.step.Step;

public class Read extends SignalStep{

    final private Expectation expectation;
    public Read(int line, String type, String name, Signal signal, Expectation expectation) {
        super(line, type, name, signal);
        this.expectation = expectation;
    }

    public Read(Step step, Signal signal, Expectation expectation) {
        super(step.getLine(), step.getType(), step.getName(), signal);
        this.expectation = expectation;
    }

    public Expectation getExpectation() {
        return expectation;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        if (expectation != null) {
            jsonObject.addProperty("Expectation", expectation.toString());
        }
        return jsonObject;
    }

}
