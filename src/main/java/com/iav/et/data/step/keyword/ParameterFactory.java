package com.iav.et.data.step.keyword;

import com.google.gson.JsonObject;
import com.iav.utils.JsonUtils;
import de.tracetronic.tts.apiClient.stubs.TsKeyword;
import de.tracetronic.tts.apiClient.stubs.TsKeywordArgument;

import java.util.HashMap;
import java.util.Objects;

public class ParameterFactory {

    public static HashMap<String, Parameter> buildStringParameterHashMapFromApi(TsKeyword tsKeyword) {
        HashMap<String, Parameter> parameterHashMap = new HashMap<>();
        for (TsKeywordArgument tsKeywordArgument : tsKeyword.GetArguments()) {
            Parameter parameter = new Parameter(
                    tsKeywordArgument.GetIdentifier(), tsKeywordArgument.GetName(), tsKeywordArgument.GetValueExpression()
            );
            parameterHashMap.put(parameter.getId(), parameter);
        }
        return parameterHashMap;
    }

    public static HashMap<String, Parameter> buildStringParameterHashMapFromJsonObject(JsonObject jsonObject) {
        HashMap<String, Parameter> parameterHashMap = new HashMap<>();
        for (String parameterId : jsonObject.keySet()) {
            JsonObject parameterJsonObject = jsonObject.getAsJsonObject(parameterId);
            String name = JsonUtils.getStringAttribute(parameterJsonObject, "Name");
            String value = JsonUtils.getStringAttribute(parameterJsonObject, "Value");
//                    replace("\\\\", "\\").replace("\\\\n", "\\n").replace("\\\"","\"");
            Parameter parameter = new Parameter(parameterId, name, value);
            parameterHashMap.put(parameter.getId(), parameter);
        }
        return parameterHashMap;
    }
}
