package com.iav.et.data.etpackage;

import com.google.gson.JsonObject;
import com.iav.et.api.Api;
import com.iav.et.data.step.keyword.Keyword;
import com.iav.et.data.step.keyword.Parameter;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.utils.JsonUtils;
import de.tracetronic.tts.apiClient.stubs.Package;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EtPackageFactoryTest {

    @BeforeAll
    public static void setUp() {
        try {
            Workspace.setUp(WorkspaceType.BK25);
            GloMa.setUp();
            VarFile.setUp();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void buildPackageFromPackagePathTestFromTestPackage() {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_testCase.pkg";
        try {
            EtPackage p = EtPackageFactory.buildPackageFromPackagePath(path);
            Assertions.assertEquals("TestPackage_testCase", p.getName());
            Assertions.assertEquals("000000", p.getId());
        } catch (NoSuchFileException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(NoSuchFileException.class, () -> {Api.openPackage("c:\\noSuchFile");});
    }

    @Test
    public void buildPackageFromPackagePathTestFromRealPackage() {
        String path = "C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-10\\TSI_FKT_ELSV_EinUndAusstiegsfahrtMitVerstellung.pkg";
//        String path = "C:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP\\_TSI_overall\\R23-09\\TSI_FKT_ABS_AusfallCcmNotEbv.pkg";
        long startTime = System.nanoTime();
        long endTimeOpenPackage;
        long endTimeParsePackage;
        try {
            Package p = Api.openPackage(path);
            endTimeOpenPackage = System.nanoTime();
            EtPackage etPackage = EtPackageFactory.buildPackageFromApi(p);
            endTimeParsePackage = System.nanoTime();
        } catch (NoSuchFileException exception) {
            throw new RuntimeException(exception);
        }
        long durationOpenPackage = endTimeOpenPackage - startTime;
        long durationParsePackage = endTimeParsePackage - startTime;
        System.out.println("Execution time open package in seconds: " + durationOpenPackage / 1_000_000_000.0);
        System.out.println("Execution time parse to package in seconds: " + durationParsePackage / 1_000_000_000.0);
    }

    @Test
    public void buildEtPackageFromJsonObjectTest() throws FileNotFoundException {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\json\\TestPackage_testCase.json";
//        String path = "c:\\src\\JsonRepository\\BK25\\testcases\\TSI_FKT_EHC_NiveauausgleichFahrt.json";
        JsonObject jsonObject = JsonUtils.parseJsonFile(path);
        EtPackage p = EtPackageFactory.buildPackageFromJsonObject(jsonObject);
        Assertions.assertEquals("000000", p.getId());
        Assertions.assertEquals("TestPackage_testCase", p.getName());
        Assertions.assertEquals("1", p.getVersion());
        p.flat();
        Assertions.assertEquals(23, p.getSize());
    }

//    @Test
//    public void buildPackagesFromDirectoryWithJsonFilesTest() throws FileNotFoundException {
//        String path = "c:\\src\\JsonRepository\\BK25";
//        List<EtPackage> packages = EtPackageFactory.buildListFromDirectoryWithJsonFiles(path);
//        for (EtPackage p : packages) {
//            p.flat();
//        }
//        HashMap<String, EtPackage> hashMap = EtPackageFactory.buildHashMapFromDirectoryWithJsonFiles(path);
//        // Assertions.assertEquals(hashMap.size(), packages.size()); // TODO: buildFromDirectory -> recursive parameter
//        // recursive is by default now
//    }

    @Test
    public void buildEtPackageFromPackageObjectThrowsNoSuchFileException() {
        Assertions.assertThrows(NoSuchFileException.class, () -> {Api.openPackage("c:\\noSuchFile");});
    }

    @Test
    public void getKeywordIdTest() throws NoSuchFileException {
        String path = "C:\\src\\BK25_Workspace\\Packages\\KWI\\Control\\DD_TSP\\Acceleration_Pedal within_TSP_EES25.pkg";
        EtPackage keywordPackage = EtPackageFactory.buildPackageFromPackagePath(path);
        Assertions.assertEquals("43362", keywordPackage.getId());
    }

    @Test
    public void parseKeywordPackageTest() throws NoSuchFileException {
        String path = "C:\\src\\BK25_Workspace\\Packages\\KWI\\Control\\DD_TSP\\Crash_ACSM_TSP_EES25.pkg";
        EtPackage keywordPackage = EtPackageFactory.buildPackageFromPackagePath(path);
        Assertions.assertEquals("43147", keywordPackage.getId());
    }

    @Test
    public void buildEtPackagesFromJsonRepositorySP21() throws FileNotFoundException {
        String path = "C:\\src\\JsonRepository\\SP21";
        List<EtPackage> packages = EtPackageFactory.buildListFromDirectoryWithJsonFiles(path);
        Assertions.assertEquals(957, packages.size());
    }

    @Test
    public void buildFromJsonFileTest() throws FileNotFoundException {
        String path = "C:\\src\\JsonRepository\\BK25\\testcases\\TSI_FKT_ABS_AusfallCcm.json";
        EtPackage etPackage = EtPackageFactory.buildFromJsonFile(path);
        for (Keyword keyword : etPackage.getKeywordSteps()) {
            for (String id : keyword.getParameterHashMap().keySet()) {
                Parameter parameter = keyword.getParameterHashMap().get(id);
                System.out.println(parameter.getId()+" "+parameter.getName()+" "+parameter.getValue());
            }
        }
    }

    @Test
    public void buildPackageFromJsonAndWriteBackToJsonTest() {
        String path = "C:\\src\\JsonRepository\\BK25\\testcases\\TSI_FKT_ABS_AusfallCcm.json";
    }
}
