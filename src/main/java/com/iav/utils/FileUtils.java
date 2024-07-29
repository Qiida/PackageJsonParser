package com.iav.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

final public class FileUtils {

    private FileUtils() {}
    public static List<String> findFilesByTypeRecursively(Path startPath, String fileType) {
        List<String> matchedPaths = new ArrayList<>();
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes basicFileAttributes) {
                    if (file.toString().endsWith(fileType)) {
                        matchedPaths.add(file.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    System.err.println(e);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchedPaths;
    }
    public static List<String> findFilesByTypeRecursively(String startPath, String fileType) {
        return findFilesByTypeRecursively(Paths.get(startPath), fileType);
    }

    public static List<String> findFilesByType(String path, String fileType) {
        File directory = new File(path);
        File[] foundFiles = directory.listFiles();
        List<String> foundFilePaths = new ArrayList<>();
        if (foundFiles != null) {
            for (File foundFile : foundFiles) {
                if (foundFile.isFile()) {
                    if (foundFile.getName().endsWith(fileType)) {
                        foundFilePaths.add(foundFile.getAbsolutePath());
                    }
//                    System.out.println("name: " + foundFile.getName());
//                    System.out.println("path: " + foundFile.getPath());
//                    System.out.println("absolute path: " + foundFile.getAbsolutePath());
                }
            }
        }
        return foundFilePaths;
    }

    public static boolean isInvalidPath(String path) {
        try {
            return !Files.exists(Paths.get(path));
        } catch (InvalidPathException | NullPointerException exception) {
            return true;
        }
    }

    public static String getRelativePath(String root, String path) {
        Path rootPath = Path.of(root);
        Path relative = rootPath.relativize(Path.of(path));
        return relative.toString();
    }
}