package com.iav.et.data.expectation;

import com.iav.et.api.Api;
import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.data.step.Step;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;

import de.tracetronic.tts.apiClient.stubs.Package;
import de.tracetronic.tts.apiClient.stubs.TestStep;
import de.tracetronic.tts.apiClient.stubs.TsRead;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

public class ExpectationFactoryTest {

    static EtPackage PACKAGE;
    final static String PATH = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_expectation.pkg";

    @BeforeAll
    public static void setUp() {
        try {
            Workspace.setUp(WorkspaceType.BK25);
            GloMa.setUp();
            VarFile.setUp();
        } catch (IllegalStateException ignored) {}
        try {
            PACKAGE = EtPackageFactory.buildPackageFromPackagePath(PATH);
        } catch (NoSuchFileException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void buildExpectationFromApiTest() throws NoSuchFileException {
        //de.tracetronic.tts.apiClient.stubs.Expectation e
        Step step = PACKAGE.getStepByLine(4);
        Package api = Api.openPackage(PATH);
        TestStep testStep = api.GetTestStepByLineNo(4);
        if (testStep instanceof TsRead tsRead) {
            de.tracetronic.tts.apiClient.stubs.Expectation apiExpectation = tsRead.GetExpectation();
            Expectation expectation = ExpectationFactory.buildExpectationFromApi(apiExpectation);
            System.out.println("The End.");
        }
    }
}
