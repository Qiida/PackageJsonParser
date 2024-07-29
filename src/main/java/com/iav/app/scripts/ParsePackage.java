package com.iav.app.scripts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.iav.et.api.Api;
import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.jira.JiraIntegration;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

import static com.iav.et.api.Api.LOGGER;

public class ParsePackage {

    final static private WorkspaceType WORKSPACETYPE = WorkspaceType.BK25;
    final static private String PATH_IN =  "\\Packages\\KWI\\Status\\DD_TSP\\CCM_VDM_TSP_EES25.pkg";
    final static private String PATH_OUT = "c:\\src\\JsonRepository\\BK25\\keywords\\status\\CCM_VDM_TSP_EES25.json";

    public static void main(String[] args) throws NoSuchFileException {
        Workspace.setUp(WORKSPACETYPE);
        GloMa.setUp();
        VarFile.setUp();
        JiraIntegration.setUp();
        EtPackage etPackage = EtPackageFactory.buildPackageFromPackagePath(Workspace.getPATH()+PATH_IN);
        JsonObject jsonObject = etPackage.toJson();
        try  {
            String file = PATH_OUT;
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
        System.out.println("This is the End.");
    }
}
