package com.iav.et.workspace;

public class Workspace {
    private static Workspace INSTANCE = null;
    private final static String BK25_WORKSPACE_DIR = "C:\\src\\BK25_Workspace";
    private final static String BK25_MAFI_PATH = "C:\\src\\BK25_Workspace\\Parameters\\mafi\\DD_TSP\\DD_TSP_EES25.mafi";
    private final static String SP21_WORKSPACE_DIR = "C:\\src\\SP21_Workspace_MEGA";
    private final static String SP21_MAFI_PATH = "C:\\src\\SP21_Workspace_MEGA\\Parameters\\TSP.mafi";

    public final WorkspaceType type;
    private String path;

    private Workspace(WorkspaceType type) {
        this.type = type;
        switch (type) {
            case BK25 -> path = BK25_WORKSPACE_DIR;
            case SP21 -> path = SP21_WORKSPACE_DIR;
        }
    }

    public String getMafiPath() {
        return switch (type) {
            case BK25 -> BK25_MAFI_PATH;
            case SP21 -> SP21_MAFI_PATH;
        };
    }

    public String getPath() {
        return path;
    }

    public static void setUp(WorkspaceType type) {
        if (INSTANCE == null) {
            INSTANCE = new Workspace(type);
        } else {
            throw new IllegalStateException("Workspace already initialized");
        }
    }

    public static Workspace getINSTANCE() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Workspace not initialized, call setUp() first");
        }
        return INSTANCE;
    }

    public static String getPATH() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Workspace not initialized, call setUp() first");
        }
        return INSTANCE.getPath();
    }

    public static WorkspaceType getType() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Workspace not initialized, call setUp() first");
        }
        return INSTANCE.type;
    }
}
