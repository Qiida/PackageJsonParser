package com.iav.et.data.step.flow;

import com.google.gson.JsonObject;
import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepContainer;

import java.util.LinkedHashMap;

public class Loop extends StepContainer {
    private final String count;

    public Loop(int line, String type, String name, LinkedHashMap<String, Step> stepLinkedHashMap, String count) {
        super(line, type, name, stepLinkedHashMap);
        this.count = count;
    }

    public Loop(Step step, LinkedHashMap<String, Step> stepLinkedHashMap, String count) {
        super(step.getLine(), step.getType(), step.getName(), stepLinkedHashMap);
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("Count", count);
        return jsonObject;
    }
}
