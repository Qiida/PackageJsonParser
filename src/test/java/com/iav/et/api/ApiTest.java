package com.iav.et.api;

import de.tracetronic.tts.apiClient.stubs.ApiClient;
import de.tracetronic.tts.apiClient.stubs.Package;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

public class ApiTest {
    @Test
    public void openPackageTest() {
        try {
            Package p = Api.openPackage("c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_testCase.pkg");
            Assertions.assertInstanceOf(Package.class, p);
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
        Assertions.assertThrows(NoSuchFileException.class, () -> {Api.openPackage("c:\\src\\TA\\src\\test\\resources\\package\\TestPackage_testCase");} );
    }

}
