package com.iav.app.jsonparser.automation;

import com.iav.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public record Batch(List<Unit> units) {

    public static List<Batch> buildBatchesFromUnitList(List<Unit> units, int size) {
        int numberOfUnits = units.size();
        List<Batch> batches = new ArrayList<>();
        List<Unit> unitsForBatch = new ArrayList<>();
        for (int i = 0; i < units.size(); i++) {
            if ((unitsForBatch.size() < size - 1) && (i < numberOfUnits - 1)) {
                unitsForBatch.add(units.get(i));
            } else if ((unitsForBatch.size() < size - 1) && (i == numberOfUnits - 1)) {
                unitsForBatch.add(units.get(i));
                batches.add(new Batch(new ArrayList<>(unitsForBatch)));
            } else if (unitsForBatch.size() == size - 1) {
                unitsForBatch.add(units.get(i));
                batches.add(new Batch(new ArrayList<>(unitsForBatch)));
                unitsForBatch.clear();
            }
        }
        return batches;
    }

    public static List<Batch> buildBatchesFromDirectoryPathLists(List<String> inputDirectoryPathList, List<String> outputDirectoryPathList, int size, boolean recursive) {
        if (inputDirectoryPathList.size() != outputDirectoryPathList.size()) {
            throw new IllegalArgumentException("inputPaths and outputPaths must have the same size");
        }
        List<Unit> units = new ArrayList<>();
        for(int i=0; i<inputDirectoryPathList.size(); i++) {
            String inputPath = inputDirectoryPathList.get(i);
            String outputPath = outputDirectoryPathList.get(i);
            List<String> filePaths;
            if (recursive) {
                filePaths = FileUtils.findFilesByTypeRecursively(inputPath, "pkg");
            } else {
                filePaths = FileUtils.findFilesByType(inputPath, "pkg");
            }
            for (String filePath : filePaths) {
                units.add(new Unit(filePath, outputPath));
            }
        }
        return Batch.buildBatchesFromUnitList(units, size);
    }
    public static List<Batch> buildBatchesFromFilePathLists(List<String> inputPathList, List<String> outputPathList) {
        if (inputPathList.size() != outputPathList.size()) {
            throw new IllegalArgumentException("inputPaths and outputPaths must have the same size");
        }
        List<Batch> batches = new ArrayList<>();
        List<Unit> units = new ArrayList<>();
        for(int i=0; i< inputPathList.size(); i++) {
            units.add(new Unit(inputPathList.get(i), outputPathList.get(i)));
        }
        batches.add(new Batch(units));
        return batches;
    }

}
