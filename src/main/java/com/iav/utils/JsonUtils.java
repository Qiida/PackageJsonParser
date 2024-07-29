package com.iav.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iav.et.data.step.Step;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;

final public class JsonUtils {

    private JsonUtils() {}
    public static JsonObject createJsonObjectFromStepLinkedHashMap(LinkedHashMap<String, Step> stepLinkedHashMap) {
        JsonObject jsonObject = new JsonObject();
        for (String k : stepLinkedHashMap.keySet()) {
            Step step = stepLinkedHashMap.get(k);
            jsonObject.add(step.createKey(), step.toJson());
        }
        return jsonObject;
    }

    public static JsonObject parseJsonFile(String filePath) throws FileNotFoundException {
        return new GsonBuilder().disableHtmlEscaping().create().fromJson(new FileReader(filePath), JsonObject.class);
    }

    public static String getStringAttribute(JsonObject jsonObject, String key) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement != null) {
            String stringAttribute = jsonElement.toString();
            stringAttribute = stringAttribute.substring(1, stringAttribute.length() - 1);
            stringAttribute = stringAttribute.replace("\\\\", "\\");
            stringAttribute = stringAttribute.replace("\\\"", "\"");
            return stringAttribute;
        } else {
            return null;
        }
    }
}
