package com.iav.et.data.step.keyword;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Parameter {
    final private String id;
    final private String name;
    final private String value;

    public Parameter(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public JsonElement toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Value", value.replace("\\\\", "\\"));
        return jsonObject;
    }
}
