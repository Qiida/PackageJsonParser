package com.iav.app.scripts;

import com.iav.app.jsonparser.JsonParser;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;

import java.util.ArrayList;
import java.util.List;

public class ParsePackages_SP21 {
    public static void main(String[] args) {
        Workspace.setUp(WorkspaceType.SP21);
        GloMa.setUp();
        VarFile.setUp();
        parseTestCasesAndKeywords();
    }

    public static void parseTestCasesAndKeywords() {
        List<String> inputPathList = new ArrayList<>();
        inputPathList.add("C:\\src\\SP21_Workspace_MEGA\\Packages\\03_Schluesselwoerter\\Steuern");
        inputPathList.add("C:\\src\\SP21_Workspace_MEGA\\Packages\\03_Schluesselwoerter\\Status");
        inputPathList.add("C:\\src\\SP21_Workspace_MEGA\\Packages\\03_Schluesselwoerter\\Herstellen");
        inputPathList.add("C:\\src\\SP21_Workspace_MEGA\\Packages\\02_Testskripte\\01_Allgemein_SP2021\\_TSI_overall");
        inputPathList.add("C:\\src\\SP21_Workspace_MEGA\\Packages\\08_Bibliothek");


        List<String> outputPathList = new ArrayList<>();
        outputPathList.add("C:\\src\\JsonRepository_MEGA\\SP21\\keywords\\control");
        outputPathList.add("C:\\src\\JsonRepository_MEGA\\SP21\\keywords\\status");
        outputPathList.add("C:\\src\\JsonRepository_MEGA\\SP21\\keywords\\establish");
        outputPathList.add("C:\\src\\JsonRepository_MEGA\\SP21\\testcases");
        outputPathList.add("C:\\src\\JsonRepository_MEGA\\SP21\\library");
        new JsonParser(inputPathList, outputPathList, 50, true).process();
    }
}
