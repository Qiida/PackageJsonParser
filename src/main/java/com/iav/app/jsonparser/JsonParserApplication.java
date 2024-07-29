package com.iav.app.jsonparser;

import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonParserApplication {

    public static void main(String[] args) throws IOException {
        Workspace.setUp(WorkspaceType.BK25);
        GloMa.setUp();
        VarFile.setUp();
        System.out.println();
        System.out.println(
        """
                ██╗ █████╗ ██╗   ██╗       ██╗███████╗ ██████╗ ███╗   ██╗██████╗  █████╗ ██████╗ ███████╗███████╗██████╗\s
                ██║██╔══██╗██║   ██║       ██║██╔════╝██╔═══██╗████╗  ██║██╔══██╗██╔══██╗██╔══██╗██╔════╝██╔════╝██╔══██╗
                ██║███████║██║   ██║       ██║███████╗██║   ██║██╔██╗ ██║██████╔╝███████║██████╔╝███████╗█████╗  ██████╔╝
                ██║██╔══██║╚██╗ ██╔╝  ██   ██║╚════██║██║   ██║██║╚██╗██║██╔═══╝ ██╔══██║██╔══██╗╚════██║██╔══╝  ██╔══██╗
                ██║██║  ██║ ╚████╔╝██╗╚█████╔╝███████║╚██████╔╝██║ ╚████║██║     ██║  ██║██║  ██║███████║███████╗██║  ██║
                ╚═╝╚═╝  ╚═╝  ╚═══╝ ╚═╝ ╚════╝ ╚══════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═╝
                                                                                                                        \s"""
        );
        IO io = getIO(args);
        new JsonParser(io.inputPaths(), io.outputPaths(), io.batchSize(), io.recursive()).process();
    }

    private static IO getIO(String[] args) throws IOException {
        for (String arg : args) {
            System.out.println(arg);
        }
        List<String> inputPaths = new ArrayList<>();
        List<String> outputPaths = new ArrayList<>();
        int batchSize = 50;
        boolean recursive = false;
        for (int i = 0; i< args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-batch": {
                    batchSize = Integer.parseInt(args[i+1]);
                    break;
                }
                case "-in":
                    populatePathList(args, i, inputPaths);
                    break;
                case "-out":
                    populatePathList(args, i, outputPaths);
                    break;
                case "-recursive":
                    recursive = true;
                    break;
            }
        }
        if (outputPaths.size() == 1 && inputPaths.size() > 1) {
            for (int i=0; i< inputPaths.size() - 1; i++) {
                outputPaths.add(outputPaths.getFirst());
            }
        }
        if (inputPaths.size() != outputPaths.size()) {
            throw new IOException("Number of input and output paths must be the same");
        }
        return new IO(inputPaths, outputPaths, batchSize, recursive);
    }

    private static void populatePathList(String[] args, int i, List<String> inputPaths) {
        for (int j = i; j< args.length; j++) {
            if (j != args.length - 1) {
                String inputPath = args[j+1];
                if (inputPath.startsWith("-")) {
                    break;
                } else {
                    inputPaths.add(inputPath);
                }
            }
        }
    }

    private record IO(List<String> inputPaths, List<String> outputPaths, Integer batchSize, Boolean recursive) {}
}
