package com.iav.et.data.step.flow;

import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepContainer;


import java.util.LinkedHashMap;

public class Block extends StepContainer {

    public Block(int line, String type, String name, LinkedHashMap<String, Step> stepLinkedHashMap) {
        super(line, type, name, stepLinkedHashMap);

    }
    public Block(Step step, LinkedHashMap<String, Step> stepLinkedHashMap) {
        super(step.getLine(), step.getType(), step.getName(), stepLinkedHashMap);
    }

}
