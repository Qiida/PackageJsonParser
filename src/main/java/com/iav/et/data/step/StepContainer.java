package com.iav.et.data.step;

import com.google.gson.JsonObject;
import com.iav.utils.JsonUtils;


import java.util.LinkedHashMap;

public abstract class StepContainer extends Step{

    final private LinkedHashMap<String, Step> stepLinkedHashMap;

    private LinkedHashMap<Integer, Step> flatLinkedHashMap = null;
    public StepContainer(int line, String type, String name, LinkedHashMap<String, Step> stepLinkedHashMap) {
        super(line, type, name);

        this.stepLinkedHashMap = stepLinkedHashMap;
    }

    public LinkedHashMap<String, Step> getStepLinkedHashMap() {
        return stepLinkedHashMap;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("Steps", JsonUtils.createJsonObjectFromStepLinkedHashMap(stepLinkedHashMap));
        return jsonObject;
    }

    public LinkedHashMap<Integer, Step> flat() {
        if (flatLinkedHashMap == null) {
            flatLinkedHashMap = Step.flat(stepLinkedHashMap);
        }
        return flatLinkedHashMap;
    }

    public int size() {
        return flat().size();
    }
}
