package com.iav.app.jsonparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.iav.app.jsonparser.automation.Batch;
import com.iav.app.jsonparser.automation.Unit;
import com.iav.et.api.Api;
import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.utils.LoggerUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JsonParser {

    final private static Logger LOGGER = LoggerUtils.getLOGGER();
    final private List<Batch> batches;
    final private int numberOfBatches;

    public JsonParser(List<Batch> batches) {
        this.batches = batches;
        this.numberOfBatches = batches.size();
    }

    public JsonParser(List<String> inputPathList, List<String> outputPathList, int batchSize, boolean recursive) {
        try {
            batches = Batch.buildBatchesFromDirectoryPathLists(inputPathList, outputPathList, batchSize, recursive);
            numberOfBatches = batches.size();
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(exception);
        }
    }

    public JsonParser(String inputPath, String outputPath) throws NoSuchFileException {
        batches = null;
        numberOfBatches = 0;
        EtPackage etPackage = EtPackageFactory.buildPackageFromPackagePath(inputPath);
        JsonObject jsonObject = etPackage.toJson();
        try {
            String file = outputPath + "\\" + etPackage.getName() + ".json";
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("  ");
            gson.toJson(jsonObject, jsonWriter);
            jsonWriter.close();
            LOGGER.info(file + " written");
        } catch (IOException exception) {
            LOGGER.severe(exception.getMessage());
        }


    }

    public void process() {
        for (int i=0; i<numberOfBatches; i++) {
            Batch batch = batches.get(i);
            LOGGER.info("Process Batch " + (i+1) + "/" + numberOfBatches);
            try {
                openPackages(batch);
                parsePackages(batch);
                parseToJson(batch);
                writeJson(batch);
            } catch (Exception exception) {
                LOGGER.severe(exception.getMessage());
            }
        }
    }

    public void processSequential() {
        for (int i=0; i<numberOfBatches; i++) {
            Batch batch = batches.get(i);
            LOGGER.info("Process Batch " + (i+1) + "/" + numberOfBatches);
            for (Unit unit : batch.units()) {
                try {
                    unit.setApiPackage(Api.openPackage(unit.inputPath()));
                }
                catch(NoSuchFileException exception){
                    LOGGER.severe(exception.getMessage());
                }
                EtPackage p = EtPackageFactory.buildPackageFromApi(unit.getApiPackage());
                unit.setEtPackage(p);
                unit.setJsonObject(unit.getEtPackage().toJson());
                try  {
                    String file = unit.outputPath() + "\\" + unit.getEtPackage().getName() + ".json";
                    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    JsonWriter jsonWriter = new JsonWriter(writer);
                    jsonWriter.setIndent("    ");
                    gson.toJson(unit.getJsonObject(), jsonWriter);
                    jsonWriter.close();
                    LOGGER.info(file + " written");
                } catch (IOException exception) {
                    LOGGER.severe(exception.getMessage());
                }
            }
        }
    }

    private static void writeJson(Batch batch) {
        for (Unit unit : batch.units()) {
            try  {
                String file = unit.outputPath() + "\\" + unit.getEtPackage().getName() + ".json";
                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                JsonWriter jsonWriter = new JsonWriter(writer);
                jsonWriter.setIndent("  ");
                gson.toJson(unit.getJsonObject(), jsonWriter);
                jsonWriter.close();
                LOGGER.info(file + " written");
            } catch (IOException exception) {
                LOGGER.severe(exception.getMessage());
            }
        }
    }

    private static void parseToJson(Batch batch) {
        for (Unit unit : batch.units()) {
            unit.setJsonObject(unit.getEtPackage().toJson());
        }
    }

    private static void parsePackages(Batch batch) {
        for (Unit unit : batch.units()) {
            EtPackage p = null;
            while(p == null) {
                try {
                    p = EtPackageFactory.buildPackageFromApi(unit.getApiPackage());
                    unit.setEtPackage(p);
                } catch(Exception exception) {
                    LOGGER.warning(exception.getMessage());
                    LOGGER.warning("Try again");
                }
            }
        }
    }

    private static void openPackages(Batch batch) {
        for (Unit unit : batch.units()) {
            try {
                unit.setApiPackage(Api.openPackage(unit.inputPath()));
            }
            catch(NoSuchFileException exception){
                LOGGER.severe(exception.getMessage());
            }
        }
    }


    public static void main(String[] args) throws NoSuchFileException {
        Workspace.setUp(WorkspaceType.BK25);
        GloMa.setUp();
        VarFile.setUp();

        List<String> inputPathList = new ArrayList<>();
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-09");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-10");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-11");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-01");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-02");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-03");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-06");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-09");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Status\\DD_TSP");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Establish\\DD_TSP");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Control\\DD_TSP");

        List<String> outputPathList = new ArrayList<>();
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\status");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\establish");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\control");

        new JsonParser(inputPathList, outputPathList, 30, false).process();

//        String in = "C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-02\\TSI_INT_ROBUSTHEIT_Kurzschluss_FlexRay_ARS.pkg";
//        String out = "C:\\src";
//        new JsonParser(in, out);
    }
}
