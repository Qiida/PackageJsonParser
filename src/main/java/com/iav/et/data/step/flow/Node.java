package com.iav.et.data.step.flow;

import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepContainer;

import java.util.LinkedHashMap;

public class Node extends StepContainer {
    public Node(int line, String type, String name, LinkedHashMap<String, Step> stepLinkedHashMap) {
        super(line, type, name, stepLinkedHashMap);
    }

    public Node(Step step, LinkedHashMap<String, Step> stepLinkedHashMap) {
        super(step.getLine(), step.getType(), step.getName(), stepLinkedHashMap);
    }
}
