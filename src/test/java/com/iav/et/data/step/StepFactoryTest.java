package com.iav.et.data.step;

import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

public class StepFactoryTest {
    @BeforeAll
    public static void setUp() {
        try {
            Workspace.setUp(WorkspaceType.BK25);
            GloMa.setUp();
            VarFile.setUp();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void buildStepsWithDifferentExpectations() {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_expectation.pkg";
        EtPackage p;
        try {
            p = EtPackageFactory.buildPackageFromPackagePath(path);
        } catch (NoSuchFileException e) {
            throw new RuntimeException(e);
        }
    }
}
