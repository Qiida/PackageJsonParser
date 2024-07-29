package com.iav.et.mapping;

import com.iav.et.api.Api;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.GlobalMapping;

import java.nio.file.NoSuchFileException;
import java.util.logging.Logger;

public class GloMa {

    private static GloMa INSTANCE = null;
    final public String path;
    public GlobalMapping api;
    private static final Logger LOGGER = LoggerUtils.getLOGGER();

    private GloMa(String path) {
        this.path = path;
        try {
            api = Api.openMapping(path);
        } catch (NoSuchFileException e) {
            LOGGER.severe("Mapping File not Found: " + path);
        }
    }
    private GloMa() {
        path = null;
        LOGGER.warning("Workspace has no GloMa");
    }
    public static void setUp() {
        if (INSTANCE == null) {
            Workspace workspace = Workspace.getINSTANCE();
            if (workspace.type == WorkspaceType.BK25) {
                INSTANCE = new GloMa(workspace.getPath() + "\\Parameters\\GloMa\\GloMa_EES25_Bus.xam");
            }
            if (workspace.type == WorkspaceType.SP21) {
                INSTANCE = new GloMa();
            }
        } else {
            throw new IllegalStateException("GloMa already initialized");
        }
    }

    public static GloMa getINSTANCE() {
        if (INSTANCE == null) {
            throw new IllegalStateException("GloMa not initialized, call setUp() first");
        }
        return INSTANCE;
    }
}
