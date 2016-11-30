package com.sdarioo.bddviewer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static void deleteFiles(List<Path> paths) {
        if (paths != null) {
            paths.forEach(FileUtil::deleteFile);
        }
    }

    public static void deleteFile(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOGGER.warn(e.toString());
            }
        }
    }

    public static void deleteDir(Path dir) {
        try {
            tryDeleteDir(dir);
        } catch (IOException e) {
            LOGGER.warn("Error while deleting directory: " + dir.toString());
        }
    }

    private static void tryDeleteDir(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.delete(dir);
    }
}
