package com.iav.app.jsonparser;


import com.iav.app.jsonparser.automation.GitAutomatorExecutor;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import com.iav.git.Update;
import com.iav.task.Task;
import com.iav.utils.LoggerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FullyAutomatedJsonParser {

    final static private Logger LOGGER = LoggerUtils.getLOGGER();

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
        System.out.println("\nThis Tool keeps the Workspace up to date. Updated Packages will be parsed to JSON files and pushed to the JsonRepository.\n");

//        new Task("c:\\src\\BK25_Workspace\\Packages\\TCI\\DD_TSP", "git reset --hard 2b80370749efcb6da69b39fd99dd331a3c2be401").execute();
//        new Task("c:\\src\\JsonRepository", "git reset --hard fca968c6e9d57189b975ae5fb3bd0d3fdec52267").execute();
        List<Thread> threads = new ArrayList<>();
        for (String path : io.paths) {
            threads.add(new Thread(new GitAutomatorExecutor(path, io.duration, io.timeUnit)));
        }
        for (Thread thread : threads) {
            thread.start();
            LOGGER.info(thread.getName()+" started");
        }
        boolean run = true;
        while(run) {
            if (!GitAutomatorExecutor.UPDATES.isEmpty()) {
                while(!GitAutomatorExecutor.UPDATES.isEmpty()) {
                    Update nextUpdate = GitAutomatorExecutor.UPDATES.poll();
                    if (nextUpdate != null) {
                        List<String> inPaths = new ArrayList<>();
                        List<String> outPaths = new ArrayList<>();
                        populateInOutPathsFromNextUpdate(nextUpdate, inPaths, outPaths);
                        if (!inPaths.isEmpty()) {
                            JsonParserFactory.buildFromFilePathList(inPaths, outPaths).process();
                            executeGitCommandInJsonRepository("git add *");

                            String cmd = createCmdString(nextUpdate);
                            executeGitCommandInJsonRepository(cmd);
                            executeGitCommandInJsonRepository("git push");
                        }
                    }
                }
            }
        }
    }

    private static String createCmdString(Update nextUpdate) {
        String commitTitle = " ";
        if (nextUpdate.getPath().contains("KWI")) {
            commitTitle = "KWI Update";
        } else if (nextUpdate.getPath().contains("TCI")) {
            commitTitle = "TCI update";
        }
        return "git commit -m \""+commitTitle+"\""+
                         " -m \"from: "+ nextUpdate.getPre().getSHA()+" "+ nextUpdate.getPre().getDate()+" "+ nextUpdate.getPre().getMessage()+"\""+
                         " -m \"  to: "+ nextUpdate.getPost().getSHA()+" "+ nextUpdate.getPost().getDate()+" "+ nextUpdate.getPost().getMessage()+"\"";
    }

    private static void executeGitCommandInJsonRepository(String CMD) {
        Task finishedTask = new Task("c:\\src\\JsonRepository", CMD).execute();
        StringBuilder outputStringBuilder = new StringBuilder();
        outputStringBuilder.append("\n").append(finishedTask.getPath()).append("> ").append(finishedTask.getCmd()).append("\n");
        for (String string : finishedTask.getOutput()) {
            outputStringBuilder.append(string).append("\n");
        }
        LOGGER.info(outputStringBuilder.toString());
    }

    private static void populateInOutPathsFromNextUpdate(Update nextUpdate, List<String> inPaths, List<String> outPaths) {
        Stack<String> changes = nextUpdate.getChanges();
        while (!changes.isEmpty()) {
            String change = changes.pop();
            String[] split = change.split("/");
            if (change.contains("DD_TSP") && change.endsWith(".pkg")) {
                if (Objects.equals(split[0], "DD_TSP")) {
                    inPaths.add(Workspace.getINSTANCE().getPath() + "\\Packages\\TCI\\" + change.replace("/", "\\"));
                    outPaths.add("c:\\src\\JsonRepository\\BK25\\testcases");
                } else if (Objects.equals(split[0], "Control")) {
                    inPaths.add(Workspace.getINSTANCE().getPath() + "\\Packages\\KWI\\" + change.replace("/", "\\"));
                    outPaths.add("c:\\src\\JsonRepository\\BK25\\keywords\\control");
                } else if (Objects.equals(split[0], "Status")) {
                    inPaths.add(Workspace.getINSTANCE().getPath() + "\\Packages\\KWI\\" + change.replace("/", "\\"));
                    outPaths.add("c:\\src\\JsonRepository\\BK25\\keywords\\status");
                } else if (Objects.equals(split[0], "Establish")) {
                    inPaths.add(Workspace.getINSTANCE().getPath() + "\\Packages\\KWI\\" + change.replace("/", "\\"));
                    outPaths.add("c:\\src\\JsonRepository\\BK25\\keywords\\establish");
                }

            }
        }
    }
    private static IO getIO(String[] args) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        for (String arg : args) {
            inputStringBuilder.append(arg).append(" ");
        }
        System.out.println(inputStringBuilder);
        List<String> paths = new ArrayList<>();
        int duration = 60;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        for (int i = 0; i< args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-paths":
                    populatePathList(args, i, paths);
                    break;
                case "-duration":
                    duration = Integer.parseInt(args[i+1]);
                    break;
                case "-unit":
                    switch (args[i+1]) {
                        case "minutes":
                            break;
                        case "seconds":
                            timeUnit = TimeUnit.SECONDS;
                            break;
                        case "hours":
                            timeUnit = TimeUnit.HOURS;
                            break;
                        default:
                            throw new IOException("Unknown time unit: "+args[i+1]+"\nAvailable: hours, minutes, seconds");
                    }
            }
        }
        return new IO(paths, duration, timeUnit);
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

    private record IO(List<String> paths, int duration, TimeUnit timeUnit) {}

}
