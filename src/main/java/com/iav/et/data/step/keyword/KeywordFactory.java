package com.iav.et.data.step.keyword;

import com.google.gson.JsonObject;
import com.iav.et.data.expectation.Expectation;
import com.iav.et.data.expectation.ExpectationFactory;
import com.iav.et.data.step.Step;
import com.iav.et.workspace.Workspace;
import com.iav.mafi.Mafi;
import com.iav.utils.JsonUtils;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.TsKeyword;
import de.tracetronic.tts.apiClient.stubs.TsKeywordReturn;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KeywordFactory {

    private static final Logger LOGGER = LoggerUtils.getLOGGER();
    private static final Pattern LONG_ID_PATTERN = Pattern.compile("Failed to parse long value: (\\d+)");

    public static Keyword buildKeywordFromApi(TsKeyword tsKeyword) {
        TsKeywordReturn[] keywordReturns = tsKeyword.GetReturns();
        Expectation expectation = null;
        if (keywordReturns.length != 0) {
            if (keywordReturns.length == 1) {
                try {
                    expectation = ExpectationFactory.buildExpectationFromApi(keywordReturns[0].GetExpectation());
                } catch (UnsupportedOperationException e) {
                    LOGGER.warning(e.getMessage());
                }
            } else {
                LOGGER.warning("KeywordReturns.length > 1: Not implemented yet!");
            }
        }
        Integer keywordId = null;
        String keywordId_string = null;
        try {
            keywordId = tsKeyword.GetKeywordId();
            keywordId_string = keywordId.toString();
        } catch (RuntimeException runtimeException) {
            LOGGER.warning("["+tsKeyword.GetLineNo()+"] KeywordId could not be parsed via api!");
            // TODO: Workaround to get ID from error message
            String message = runtimeException.getMessage();
            Matcher m = LONG_ID_PATTERN.matcher(message);
            if (m.find()) {
                keywordId_string = m.group(1);
            }
        }

        return new Keyword(
                tsKeyword.GetLineNo(), tsKeyword.GetType(), tsKeyword.GetActionColumnText(), keywordId_string,
                ParameterFactory.buildStringParameterHashMapFromApi(tsKeyword), expectation
        );
    }

    public static Keyword buildKeywordFromJsonObject(JsonObject jsonObject) {
        Expectation expectation = null;
        if (jsonObject.has("Expectation")) {
            String expectationString = JsonUtils.getStringAttribute(jsonObject, "Expectation");
            expectation = ExpectationFactory.buildExpectationFromString(expectationString);
        }
        String lineString = jsonObject.get("Line").toString();
        String type = JsonUtils.getStringAttribute(jsonObject, "Type");
        String name = JsonUtils.getStringAttribute(jsonObject, "Name");
        String id = JsonUtils.getStringAttribute(jsonObject, "Id");
        return new Keyword(
                Integer.parseInt(lineString), type, name, id,
                ParameterFactory.buildStringParameterHashMapFromJsonObject(jsonObject.getAsJsonObject("Parameter")),
                expectation
        );
    }
}
