package com.iav.jsonparser.automation;

import com.iav.app.jsonparser.automation.Batch;
import com.iav.app.jsonparser.automation.Unit;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BatchTest {

    @Test
    public void buildBatchesTest() {
        List<Unit> units = new ArrayList<>();
        units.add(new Unit("in1", "out1"));
        units.add(new Unit("in2", "out2"));
        units.add(new Unit("in3", "out3"));
        units.add(new Unit("in4", "out4"));
        units.add(new Unit("in5", "out5"));
        List<Batch> batches = Batch.buildBatchesFromUnitList(units, 2);
        Assert.assertEquals(3, batches.size());
    }
}
