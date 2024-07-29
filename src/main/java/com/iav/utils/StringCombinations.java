package com.iav.utils;

import java.util.ArrayList;
import java.util.List;

public class StringCombinations {

    public static List<String> getCombinations(String[] strings) {
        List<String> combinations = new ArrayList<>();
        permuteAndCombine(strings, 0, combinations);
        return combinations;
    }

    private static void permuteAndCombine(String[] array, int currentIndex, List<String> combinations) {
        if (currentIndex == array.length - 1) {
            combinations.add(String.join("_", array));
        }

        for (int i = currentIndex; i < array.length; i++) {
            swap(array, currentIndex, i);
            permuteAndCombine(array, currentIndex + 1, combinations);
            swap(array, currentIndex, i);
        }
    }

    private static void swap(String[] array, int index1, int index2) {
        String temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
}