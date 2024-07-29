package com.iav.et.mapping;

import com.iav.et.api.Api;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.GlobalMapping;

import java.nio.file.NoSuchFileException;
import java.util.logging.Logger;

public class VarFile {

    private static VarFile INSTANCE = null;
    private String path;
    public GlobalMapping api;
    private static  final Logger LOGGER = LoggerUtils.getLOGGER();
    private VarFile(String path) {
        try {
            api = Api.openMapping(path);

        } catch (NoSuchFileException e) {
            LOGGER.severe("Mapping File not Found: " + path);
        }
    }

    public String getPath() {
        return path;
    }

    public static void setUp() {
        if (INSTANCE == null) {
            Workspace workspace = Workspace.getINSTANCE();
            switch (workspace.type) {
                case BK25 -> INSTANCE = new VarFile(workspace.getPath() + "\\Parameters\\GloMa\\DD_TSP\\varfile.xam");
                case SP21 -> INSTANCE = new VarFile(workspace.getPath() + "\\Parameters\\varfile_MEGA_v4_0_0.xam");
            }
        } else {
            throw new IllegalStateException("VarFile already initialized");
        }
    }

    public static void setUp(String path) {
        INSTANCE = new VarFile(path);
    }

    public static VarFile getINSTANCE() {
        if (INSTANCE == null) {
            throw new IllegalStateException("VarFile not initialized, call setUp() first");
        }
        return INSTANCE;
    }
}
