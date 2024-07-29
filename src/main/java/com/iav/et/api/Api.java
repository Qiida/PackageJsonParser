package com.iav.et.api;

import com.iav.utils.FileUtils;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.ApiClient;
import de.tracetronic.tts.apiClient.stubs.GlobalMapping;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.nio.file.NoSuchFileException;
import java.util.Objects;
import java.util.logging.Logger;


public class Api {

    private static Api INSTANCE = null;

    final private ApiClient apiClient;

    final public static Logger LOGGER = LoggerUtils.getLOGGER();

    private Api() {
        apiClient = new ApiClient();
    }

    public static Api open() {
        try {
            if (INSTANCE == null) {
                INSTANCE = new Api();
            }
            return INSTANCE;
        } catch (RuntimeException exception) {
            LOGGER.severe("ECU Test must be running");
        }
        return null;
    }
    public static Package openPackage(String path) throws NoSuchFileException {
        if (FileUtils.isInvalidPath(path)) {
            throw new NoSuchFileException(path);
        }
        Package openedPackage = Objects.requireNonNull(Api.open()).apiClient.PackageApi.OpenPackage(path);
        if (openedPackage != null) {
            LOGGER.info(path + " opened");
        }
        return openedPackage;
    }

    public static GlobalMapping openMapping(String path) throws NoSuchFileException {
        if (FileUtils.isInvalidPath(path)) {
            throw new NoSuchFileException(path);
        }
        return Objects.requireNonNull(Api.open()).apiClient.GlobalMappingApi.OpenMapping(path);
    }
}
