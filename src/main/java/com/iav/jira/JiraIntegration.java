package com.iav.jira;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iav.utils.LoggerUtils;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.logging.Logger;

public class JiraIntegration {
    private static JiraIntegration INSTANCE = null;
    private static final Logger LOGGER = LoggerUtils.getLOGGER();

    final private HashMap<String, JiraIssue> testcaseSummaryIssueHashMap;
    final private HashMap<String, JiraIssue> keywordSummaryIssueHashMap;
    final private HashMap<String, JiraIssue> controlSummaryIssueHashMap;
    final private HashMap<String, JiraIssue> statusSummaryIssueHashMap;
    final private HashMap<String, JiraIssue> establishSummaryIssueHashMap;


    private JiraIntegration() {
        testcaseSummaryIssueHashMap = findTestcaseIssuesAndBuildHashMap();
        keywordSummaryIssueHashMap = findKeywordIssuesAndBuildHashMap();
        controlSummaryIssueHashMap = new HashMap<>();
        statusSummaryIssueHashMap = new HashMap<>();
        establishSummaryIssueHashMap = new HashMap<>();
        sortKeywordIssuesIntoHashMaps();
    }

    private void sortKeywordIssuesIntoHashMaps() {
        for (String summary : keywordSummaryIssueHashMap.keySet()) {
            JiraIssue jiraIssue = keywordSummaryIssueHashMap.get(summary);
            String lowerSummary = summary.toLowerCase();
            if (lowerSummary.endsWith("control")) {
                controlSummaryIssueHashMap.put(summary, jiraIssue);
            } else if (lowerSummary.endsWith("status") || (lowerSummary.endsWith("state"))) {
                statusSummaryIssueHashMap.put(summary, jiraIssue);
            } else if (lowerSummary.endsWith("establish")) {
                establishSummaryIssueHashMap.put(summary, jiraIssue);
            }
        }
    }


    private HashMap<String, JiraIssue> findTestcaseIssuesAndBuildHashMap() {
        final HashMap<String, JiraIssue> testCaseSummaryIssueHashMap;
        HttpResponse<String> response = Jira.search("project = HTT AND component = Automation_25 AND labels = FD_EES25_sWKP_TSI AND labels = TCI AND labels in (standard, komplex)", 400);
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            LOGGER.info("HTTP-Request status code "+statusCode);
        } else {
            LOGGER.severe("HTTP-Request status code "+statusCode);
        }
        JsonObject jsonObject = (JsonObject) new Gson().fromJson(response.body(), JsonElement.class);
        testCaseSummaryIssueHashMap = JiraIssueFactory.buildSummaryIssueHashMapFromJsonArray(jsonObject.getAsJsonArray("issues"));
        return testCaseSummaryIssueHashMap;
    }

    private HashMap<String, JiraIssue> findKeywordIssuesAndBuildHashMap() {
        final HashMap<String, JiraIssue> testCaseSummaryIssueHashMap;
        HttpResponse<String> response = Jira.search("project = HTT AND component = Automation_25 AND labels = KWI AND labels = TSP", 500);
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            LOGGER.info("HTTP-Request status code "+statusCode);
        } else {
            LOGGER.severe("HTTP-Request status code "+statusCode);
        }
        JsonObject jsonObject = (JsonObject) new Gson().fromJson(response.body(), JsonElement.class);
        testCaseSummaryIssueHashMap = JiraIssueFactory.buildSummaryIssueHashMapFromJsonArray(jsonObject.getAsJsonArray("issues"));
        return testCaseSummaryIssueHashMap;
    }

    public static void setUp() {
        if (INSTANCE == null) {
            INSTANCE = new JiraIntegration();
        } else {
            throw new IllegalStateException("JiraIntegration already initialized");
        }
    }

    public static JiraIntegration getINSTANCE() {
        return INSTANCE;
    }

    public static HashMap<String, JiraIssue> getKeywordSummaryIssueHashMap() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JiraIntegration not initialized, call setUp() first");
        }
        return INSTANCE.keywordSummaryIssueHashMap;
    }

    public static HashMap<String, JiraIssue> getControlSummaryIssueHashMap() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JiraIntegration not initialized, call setUp() first");
        }
        return INSTANCE.controlSummaryIssueHashMap;
    }

    public static HashMap<String, JiraIssue> getStatusSummaryIssueHashMap() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JiraIntegration not initialized, call setUp() first");
        }
        return INSTANCE.statusSummaryIssueHashMap;
    }

    public static HashMap<String, JiraIssue> getEstablishSummaryIssueHashMap() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JiraIntegration not initialized, call setUp() first");
        }
        return INSTANCE.establishSummaryIssueHashMap;
    }

    public static HashMap<String, JiraIssue> getTestcaseSummaryIssueHashMap() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JiraIntegration not initialized, call setUp() first");
        }
        return INSTANCE.testcaseSummaryIssueHashMap;
    }

}
