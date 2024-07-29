package com.iav.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FileUtilsTest {

    @Test
    public void findFilesTest() {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package";
        List<String> paths = FileUtils.findFilesByTypeRecursively(path, "pkg");
        for (String p : paths) {
            Assertions.assertTrue(p.endsWith("pkg"));
        }
    }
}
