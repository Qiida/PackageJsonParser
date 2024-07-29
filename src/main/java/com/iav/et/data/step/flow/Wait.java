package com.iav.et.data.step.flow;

import com.iav.et.data.step.Step;

public class Wait extends Step {
    final private String duration;
    public Wait(int line, String type, String name, String duration) {
        super(line, type, name);
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }
}
