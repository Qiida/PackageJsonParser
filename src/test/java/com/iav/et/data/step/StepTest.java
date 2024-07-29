package com.iav.et.data.step;

import com.google.gson.JsonObject;
import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

public class StepTest {

    @BeforeAll
    public static void setUp() {
        try {
            Workspace.setUp(WorkspaceType.BK25);
            GloMa.setUp();
            VarFile.setUp();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void toJsonTest() {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_keyword.pkg";
        EtPackage p;
        try {
            p = EtPackageFactory.buildPackageFromPackagePath(path);
        } catch (NoSuchFileException e) {
            throw new RuntimeException(e);
        }
        Step step = p.getStepByLine(1);
        JsonObject stepJson = step.toJson();
        Assertions.assertEquals(step.getName(), stepJson.get("Name").toString().replace("\"", ""));
        Assertions.assertEquals(step.getLine(), Integer.valueOf(stepJson.get("Line").toString().replace("\"", "")));
        Assertions.assertEquals(step.getType(), stepJson.get("Type").toString().replace("\"", ""));
    }
}
