package com.iav.app.jsonparser;

import com.iav.app.jsonparser.automation.Batch;

import java.util.List;

public class JsonParserFactory {
    static public JsonParser buildFromDirectoryPathLists(List<String> inputDirectoryPathList, List<String> outputDirectoryPathList, int batchSize, boolean recursive) {
        try {
            List<Batch> batches = Batch.buildBatchesFromDirectoryPathLists(inputDirectoryPathList, outputDirectoryPathList, batchSize, recursive);
            return new JsonParser(batches);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(exception);
        }
    }

    static public JsonParser buildFromFilePathList(List<String> inputPathList, List<String> outputPathList) {
        // TODO: implement batch size
        try {
            List<Batch> batches = Batch.buildBatchesFromFilePathLists(inputPathList, outputPathList);
            return new JsonParser(batches);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(exception);
        }
    }
}
