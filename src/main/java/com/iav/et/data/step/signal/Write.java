package com.iav.et.data.step.signal;

import com.iav.et.data.signal.Signal;
import com.iav.et.data.step.Step;

public class Write extends SignalStep{
    public Write(int line, String type, String name, Signal signal) {
        super(line, type, name, signal);
    }

    public Write(Step step, Signal signal) {
        super(step.getLine(), step.getType(), step.getName(), signal);
    }
}
