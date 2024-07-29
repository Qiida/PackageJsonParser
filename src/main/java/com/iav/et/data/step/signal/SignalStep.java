package com.iav.et.data.step.signal;

import com.google.gson.JsonObject;
import com.iav.et.data.step.Step;
import com.iav.et.data.signal.Signal;
public class SignalStep extends Step {

    final private Signal signal;

    public SignalStep(int line, String type, String name, Signal signal) {
        super(line, type, name);
        this.signal = signal;
    }

    public Signal getSignal() {
        return signal;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("Signal", getSignal().toJson());
        return jsonObject;
    }
}
