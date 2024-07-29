package com.iav.et.data.etpackage;

import com.google.gson.JsonObject;
import com.iav.et.api.Api;
import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepContainer;
import com.iav.et.data.step.flow.IfThenElse;
import com.iav.et.data.step.flow.SwitchCase;
import com.iav.et.data.step.keyword.Keyword;
import com.iav.et.data.step.keyword.KeywordType;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.utils.JsonUtils;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.nio.file.NoSuchFileException;
import java.util.*;

/**
 * The EtPackage class stores metadata of an ECU-test package
 * @author Qida
 */
public class EtPackage {
    final private String name;
    final private String id;
    final private String version;
    final private String path;

    private String jiraIssueKey;
    private String complexity;

    final private LinkedHashMap<String, Step> stepLinkedHashMap;
    private LinkedHashMap<Integer, Step> flatLinkedHashMap = null;

    public EtPackage(String name, String id, String version, String path, LinkedHashMap<String, Step> stepLinkedHashMap) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.path = path;
        this.stepLinkedHashMap = stepLinkedHashMap;
        Integer size = getSize();
        if (size > 50) {
            complexity = "complex";
        } else {
            complexity = "standard";
        }
    }

    public Step getStepByLine(Integer line) {
        if (flat().containsKey(line)) {
            return flat().get(line);
        } else {
            throw new NoSuchElementException("Line " + line + " not contained");
        }
    }

    public Integer getSize() {
        return flat().size();
    }

    public LinkedHashMap<Integer, Step> flat() {
        if (flatLinkedHashMap == null) {
            flatLinkedHashMap = Step.flat(stepLinkedHashMap);
        }
        return flatLinkedHashMap;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Id", id);
        jsonObject.addProperty("Version", version);
        jsonObject.addProperty("Path", path);
        jsonObject.addProperty("JiraIssueKey", jiraIssueKey);
        jsonObject.addProperty("Complexity", complexity);
        jsonObject.add("Steps", JsonUtils.createJsonObjectFromStepLinkedHashMap(stepLinkedHashMap));
        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public LinkedHashMap<String, Step> getStepLinkedHashMap() {
        return stepLinkedHashMap;
    }

    public Package openFromWorkspace() throws NoSuchFileException {
        return Api.openPackage(Workspace.getINSTANCE().getPath()+"\\"+path);
    }

    public List<Keyword> getKeywordSteps() {
        List<Keyword> keywords = new ArrayList<>();
        for (Integer line : flat().keySet()) {
            Step step = flat().get(line);
            if (step instanceof Keyword keyword) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }


    public boolean hasKeywordById(String keywordId) {
        for (Keyword keyword : getKeywordSteps()) {
            if (Objects.equals(keyword.getId(), keywordId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKeywordByName(String keywordName) {
        for (Keyword keyword : getKeywordSteps()) {
            if (Objects.equals(keyword.getName(), keywordName)) {
                return true;
            }
        }
        return false;
    }

    public Step getParentOfStep(Step step) {
        flat();
        int line = step.getLine();
        int lineCounter = line;
        while(lineCounter > 1) {
            try {
                Step s = getStepByLine(--lineCounter);
                if (s instanceof StepContainer stepContainer) {
                    if (stepContainer.flat().containsKey(line)) {
                        return s;
                    }
                } else if (s instanceof IfThenElse ifThenElse) {
                    if (ifThenElse.flat().containsKey(line)) {
                        return s;
                    }
                } else if (s instanceof SwitchCase switchCase) {
                    if (switchCase.flat().containsKey(line)) {
                        return s;
                    }
                }
            } catch (NoSuchElementException ignored) {}
        }
        return null;
    }

    public List<Step> getParentsOfStep(Step step) {
        flat();
        List<Step> parents = new ArrayList<>();
        int line = step.getLine();
        int lineCounter = line;
        while(lineCounter > 1) {
            try {
                Step s = getStepByLine(--lineCounter);
                if (s instanceof StepContainer stepContainer) {
                    if (stepContainer.flat().containsKey(line)) {
                        parents.add(s);
                    }
                } else if (s instanceof IfThenElse ifThenElse) {
                    if (ifThenElse.flat().containsKey(line)) {
                        parents.add(s);
                    }
                } else if (s instanceof SwitchCase switchCase) {
                    if (switchCase.flat().containsKey(line)) {
                        parents.add(s);
                    }
                }
            } catch (NoSuchElementException ignored) {}
        }
        return parents;
    }

    public boolean isKeywordImplementation(WorkspaceType workspaceType) {
        String lowerPath = path.toLowerCase();
        return switch (workspaceType) {
            case BK25 -> (lowerPath.contains("kwi")
                    && (lowerPath.contains("control")
                    || lowerPath.contains("status")
                    || lowerPath.contains("establish")));
            case SP21 -> (lowerPath.contains("03_schluesselwoerter")
                    && (lowerPath.contains("steuern")
                    || lowerPath.contains("status")
                    || lowerPath.contains("herstellen")));
        };
    }

    public KeywordType getKeywordType(WorkspaceType workspaceType) {
        String lowerPath = path.toLowerCase();
        switch (workspaceType) {
            case BK25 -> {
                if (lowerPath.contains("control")) {
                    return KeywordType.CONTROL;
                } else if (lowerPath.contains("status")) {
                    return KeywordType.STATUS;
                } else if (lowerPath.contains("establish")) {
                    return KeywordType.ESTABLISH;
                }
            }
            case SP21 -> {
                if (lowerPath.contains("steuern")) {
                    return KeywordType.CONTROL;
                } else if (lowerPath.contains("status")) {
                    return KeywordType.STATUS;
                } else if (lowerPath.contains("herstellen")) {
                    return KeywordType.ESTABLISH;
                }
            }
        }
        return null;
    }

    public void setJiraIssueKey(String jiraIssueKey) {
        this.jiraIssueKey = jiraIssueKey;
    }

    public String getJiraIssueKey() {
        return jiraIssueKey;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getComplexity() {
        return complexity;
    }
}
