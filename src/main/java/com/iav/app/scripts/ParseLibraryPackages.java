package com.iav.app.scripts;

import com.iav.app.jsonparser.JsonParser;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;

import java.util.ArrayList;
import java.util.List;

public class ParseLibraryPackages {
    public static void main(String[] args) {
        Workspace.setUp(WorkspaceType.BK25);
        GloMa.setUp();
        VarFile.setUp();

        List<String> inputPathList = new ArrayList<>();
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\Library\\TSP-Bibliothek");

        List<String> outputPathList = new ArrayList<>();
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\library");


        new JsonParser(inputPathList, outputPathList, 30, true).process();
    }

}
