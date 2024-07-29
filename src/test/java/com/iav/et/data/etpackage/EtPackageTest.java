package com.iav.et.data.etpackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.iav.et.data.step.Step;
import com.iav.et.data.step.keyword.Keyword;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class EtPackageTest {

    final static String PATH = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_testCase.pkg";
    static EtPackage PACKAGE = null;

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
    public void toJsonTest() throws NoSuchFileException {
        JsonObject jsonObject = PACKAGE.toJson();
        try  {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\src\\PackageJsonParser\\src\\test\\resources\\json\\" + PACKAGE.getName() + ".json"), StandardCharsets.UTF_8));
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("    ");
            gson.toJson(jsonObject, jsonWriter);
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        EtPackage andAnotherOne = EtPackageFactory.buildPackageFromPackagePath("C:\\src\\BK25_Workspace\\Packages\\KWI\\Control\\DD_TSP\\Acceleration_Pedal within_TSP_EES25.pkg");
    }

    @Test
    public void flatTest() {
        LinkedHashMap<Integer, Step> steps = PACKAGE.flat();
        Assertions.assertEquals(23, PACKAGE.getSize());
        Assertions.assertEquals(23, steps.size());
    }

    @Test
    public void getStepByLineTest() {
        Step step = PACKAGE.getStepByLine(2);
        Assertions.assertInstanceOf(Keyword.class, step);
        Assertions.assertThrows(NoSuchElementException.class, () -> PACKAGE.getStepByLine(99));
        try {
            PACKAGE.getStepByLine(24);
        } catch (NoSuchElementException e) {
            Assertions.assertEquals("Line 24 not contained", e.getMessage());
        }
    }

    @Test
    public void getKeywordStepsTest() {
        List<Keyword> keywords = PACKAGE.getKeywordSteps();
        Assertions.assertEquals("43389", keywords.get(0).getId());
        Assertions.assertEquals("43257", keywords.get(1).getId());
        Assertions.assertEquals("46063", keywords.get(2).getId());
        Assertions.assertEquals("46063", keywords.get(3).getId());
        Assertions.assertEquals("43385", keywords.get(4).getId());
    }

    @Test
    public void hasKeywordByIdTest() {
        int iterations = 1000;
        List<Long> durations = new ArrayList<>();
        for (int i=0; i<iterations; i++) {
            long start = System.nanoTime();
            Assertions.assertTrue(PACKAGE.hasKeywordById("43389"));
            Assertions.assertTrue(PACKAGE.hasKeywordById("43257"));
            Assertions.assertTrue(PACKAGE.hasKeywordById("46063"));
            Assertions.assertTrue(PACKAGE.hasKeywordById("43385"));
            long end = System.nanoTime();
            long duration = end - start;
//            System.out.println(duration);
            durations.add(duration);
        }
        Long totalDuration = 0L;
        for (Long duration : durations) {
            totalDuration += duration;
        }
        System.out.println("Mean Duration: "+totalDuration/iterations+" s");
    }

    @Test
    public void getParentOfStepTest() throws NoSuchFileException {
        EtPackage etPackage = EtPackageFactory.buildPackageFromPackagePath("C:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_familyTree.pkg");
        Step step = etPackage.getStepByLine(4);
        Step parent = etPackage.getParentOfStep(step);
        Assertions.assertEquals(3, parent.getLine());
    }

    @Test
    public void getParentsOfStepTest() throws NoSuchFileException {
        EtPackage etPackage = EtPackageFactory.buildPackageFromPackagePath("C:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_familyTree.pkg");
        Step step = etPackage.getStepByLine(4);
        List<Step> parents = etPackage.getParentsOfStep(step);
        Assertions.assertEquals(3, parents.size());
        Assertions.assertEquals("Child", parents.getFirst().getName());
        Assertions.assertEquals("Parent", parents.get(1).getName());
        Assertions.assertEquals("GrandParent", parents.get(2).getName());
    }
}
