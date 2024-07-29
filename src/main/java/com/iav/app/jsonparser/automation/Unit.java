package com.iav.app.jsonparser.automation;


import com.google.gson.JsonObject;
import com.iav.et.data.etpackage.EtPackage;
import de.tracetronic.tts.apiClient.stubs.Package;

public class Unit {
    final private String inputPath;
    final private String outputPath;

    private EtPackage etPackage = null;

    private Package apiPackage = null;

    private JsonObject jsonObject = null;
    public Unit(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void setEtPackage(EtPackage etPackage) {
        this.etPackage = etPackage;
    }

    public EtPackage getEtPackage() {
        return etPackage;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public String inputPath() {
        return inputPath;
    }

    public String outputPath() {
        return outputPath;
    }

    public Package getApiPackage() {
        return apiPackage;
    }

    public void setApiPackage(Package apiPackage) {
        this.apiPackage = apiPackage;
    }
}
