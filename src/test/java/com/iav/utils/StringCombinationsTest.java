package com.iav.utils;

import org.junit.jupiter.api.Test;

import java.util.List;


public class StringCombinationsTest {
    @Test
    public void findFilesTest() {
        String string = "emARS_actuator_torque";

        List<String> combinations = StringCombinations.getCombinations(string.split("_"));
        for (String combination : combinations) {
            System.out.println(combination);
        }
    }
}
