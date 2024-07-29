package com.iav.et.data.step.flow;

import com.google.gson.JsonObject;
import com.iav.et.data.step.Step;

import java.util.LinkedHashMap;

public class IfThenElse extends Step {

    private String condition;
    final private Node thenCase;
    final private Node elseCase;

    private LinkedHashMap<Integer, Step> flatLinkedHashMap = null;

    public IfThenElse(int line, String type, String name, String condition, Node thenCase, Node elseCase) {
        super(line, type, name);
        this.condition = condition;
        this.thenCase = thenCase;
        this.elseCase = elseCase;
    }

    public IfThenElse(Step step, String condition, Node thenCase, Node elseCase) {
        super(step.getLine(), step.getType(), step.getName());
        this.condition = condition;
        this.thenCase = thenCase;
        this.elseCase = elseCase;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("Condition", condition);
        jsonObject.add(thenCase.createKey(), thenCase.toJson());
        jsonObject.add(elseCase.createKey(), elseCase.toJson());
        return jsonObject;
    }

    public LinkedHashMap<Integer, Step> flat() {
        if (flatLinkedHashMap == null) {
            flatLinkedHashMap = new LinkedHashMap<>();
            LinkedHashMap<Integer, Step> thenLinkedHashMap = thenCase.flat();
            LinkedHashMap<Integer, Step> elseLinkedHashMap = elseCase.flat();
            flatLinkedHashMap.put(thenCase.getLine(), thenCase);
            for (Integer line : thenLinkedHashMap.keySet()) {
                flatLinkedHashMap.put(line, thenLinkedHashMap.get(line));
            }
            flatLinkedHashMap.put(elseCase.getLine(), elseCase);
            for (Integer line : elseLinkedHashMap.keySet()) {
                flatLinkedHashMap.put(line, elseLinkedHashMap.get(line));
            }
        }
        return flatLinkedHashMap;
    }


    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public Node getThenCase() {
        return thenCase;
    }

    public Node getElseCase() {
        return elseCase;
    }

}
