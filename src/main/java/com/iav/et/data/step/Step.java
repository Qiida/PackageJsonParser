package com.iav.et.data.step;

import com.google.gson.JsonObject;
import com.iav.et.data.step.flow.IfThenElse;
import com.iav.et.data.step.flow.SwitchCase;

import java.util.LinkedHashMap;


public class Step {
    private int line;
    private String type;
    private String name;

    public Step(int line, String type, String name) {
        this.line = line;
        this.type = type;
        this.name = name;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Line", this.line);
        jsonObject.addProperty("Type", this.type);
        jsonObject.addProperty("Name", this.name);
        return jsonObject;
    }

    public static LinkedHashMap<Integer, Step> flat(LinkedHashMap<String, Step> stepLinkedHashMap) {
        LinkedHashMap<Integer, Step> flat = new LinkedHashMap<>();
        for (Step step : stepLinkedHashMap.values()) {
            if (step instanceof StepContainer stepContainer) {
                flat.put(stepContainer.getLine(), stepContainer);
                for (Step stepInContainer : stepContainer.flat().values()) {
                    flat.put(stepInContainer.getLine(), stepInContainer);
                }
            } else if (step instanceof IfThenElse ifThenElse) {
                flat.put(ifThenElse.getLine(), ifThenElse);
                for (Integer line : ifThenElse.flat().keySet()) {
                    flat.put(line, ifThenElse.flat().get(line));
                }
            } else if (step instanceof SwitchCase switchCase) {
                flat.put(switchCase.getLine(), switchCase);
                for (Integer line : switchCase.flat().keySet()) {
                    flat.put(line, switchCase.flat().get(line));
                }
            } else if (step != null) {
                flat.put(step.getLine(), step);
            } else {
                flat.put(flat.size() + 1, null);
            }
        }
        return flat;
    }

    public String createKey() {
        return "[" + line + "] " + name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
