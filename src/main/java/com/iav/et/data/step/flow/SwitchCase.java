package com.iav.et.data.step.flow;

import com.google.gson.JsonObject;
import com.iav.et.data.step.Step;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SwitchCase extends Step {

    private final LinkedHashMap<String, Node> nodeLinkedHashMap;

    private LinkedHashMap<Integer, Step> flatLinkedHashMap = null;
    private String switchValue;
    public SwitchCase(int line, String type, String name, LinkedHashMap<String, Node> nodeLinkedHashMap, String switchValue) {
        super(line, type, name);
        this.nodeLinkedHashMap = nodeLinkedHashMap;
        this.switchValue = switchValue;
    }

    public SwitchCase(Step step, LinkedHashMap<String, Node> nodeLinkedHashMap, String switchValue) {
        super(step.getLine(), step.getType(), step.getName());
        this.nodeLinkedHashMap = nodeLinkedHashMap;
        this.switchValue = switchValue;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        JsonObject cases = new JsonObject();
        for (String caseValue : nodeLinkedHashMap.keySet()) {
            Node node = nodeLinkedHashMap.get(caseValue);
            cases.add(node.createKey(), node.toJson());
        }
        jsonObject.addProperty("Value", switchValue);
        jsonObject.add("Cases", cases);
        return jsonObject;
    }

    public LinkedHashMap<Integer, Step> flat() {
        if (flatLinkedHashMap == null) {
            flatLinkedHashMap = new LinkedHashMap<>();
            for (Node node : nodeLinkedHashMap.values()) {
                flatLinkedHashMap.put(node.getLine(), node);
                for (Step step : node.flat().values()) {
                    if (step != null) {
                        flatLinkedHashMap.put(step.getLine(), step);
                    } else {
                        throw new NullPointerException("Step is null");
                    }
                }

            }
        }
        return flatLinkedHashMap;
    }


    public HashMap<String, Node> getNodeLinkedHashMap() {
        return nodeLinkedHashMap;
    }

    public String getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(String switchValue) {
        this.switchValue = switchValue;
    }
}
