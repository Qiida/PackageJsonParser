package com.iav.et.data.step.keyword;

import com.google.gson.JsonObject;
import com.iav.et.data.expectation.Expectation;
import com.iav.et.data.step.Step;
import java.util.HashMap;

public class Keyword extends Step {

    private Expectation expectation;

    private final String id;
    final private HashMap<String, Parameter> parameterHashMap;
    public Keyword(int line, String type, String name, String id, HashMap<String, Parameter> parameterHashMap, Expectation expectation) {
        super(line, type, name);
        this.id = id;
        this.parameterHashMap = parameterHashMap;
        this.expectation = expectation;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("Id", id);
        JsonObject parameterJsonObject = new JsonObject();
        for (String id : parameterHashMap.keySet()) {
            Parameter parameter = parameterHashMap.get(id);
            parameterJsonObject.add(parameter.getId(), parameter.toJsonObject());
        }
        jsonObject.add("Parameter", parameterJsonObject);
        if (expectation != null) {
            jsonObject.addProperty("Expectation", expectation.toString());
        }
        return jsonObject;
    }

    public Expectation getExpectation() {
        return expectation;
    }

    public void setExpectation(Expectation expectation) {
        this.expectation = expectation;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, Parameter> getParameterHashMap() {
        return parameterHashMap;
    }
}
