package com.iav.app.scripts;

import com.iav.app.jsonparser.JsonParser;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;

import java.util.ArrayList;
import java.util.List;

public class ParsePackages_BK25 {
    public static void main(String[] args) {
        Workspace.setUp(WorkspaceType.BK25);
        GloMa.setUp();
        VarFile.setUp();
//        JiraIntegration.setUp();
        parseTestCasesAndKeywords();
    }

    public static void parseTestCasesAndKeywords() {
        List<String> inputPathList = new ArrayList<>();
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-09");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-10");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-11");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-01");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-02");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-03");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-06");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-07");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-09");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R24-10");
        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\RFZD");

//        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Status\\DD_TSP");
//        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Establish\\DD_TSP");
//        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\KWI\\Control\\DD_TSP");
//        inputPathList.add("C:\\src\\BK25_Workspace\\Packages\\Library\\TSP-Bibliothek");


        List<String> outputPathList = new ArrayList<>();
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");
        outputPathList.add("C:\\src\\JsonRepository\\BK25\\testcases");

//        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\status");
//        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\establish");
//        outputPathList.add("C:\\src\\JsonRepository\\BK25\\keywords\\control");
//        outputPathList.add("C:\\src\\JsonRepository\\BK25\\library");

        new JsonParser(inputPathList, outputPathList, 50, true).process();
    }
}
