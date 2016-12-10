package com.sdarioo.bddviewer.launcher;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.util.FileUtil;
import com.sdarioo.bddviewer.util.PathUtil;
import com.sdarioo.bddviewer.util.ProcessUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CmdLauncherClasspath {

    private static final Logger LOGGER = Logger.getInstance(CmdLauncherClasspath.class);

    private CmdLauncherClasspath() {}

    public static Set<Path> buildClasspath(Path moduleDir,
                                           Consumer<String> out,
                                           Consumer<String> err) {
        Set<Path> result = new LinkedHashSet<>();

        Path[] cp = getMavenClasspath(moduleDir, out, err);

        Map<String, Path> cpPathByName = new HashMap<>();
        for (Path path : cp) {
            String name = stripVersion(PathUtil.getName(path));
            cpPathByName.put(name, path);
        }

        // Classes and test classes from project
        Path rootDir = moduleDir.getParent();
        list(rootDir).forEach(path -> {
            Path targetDir = path.resolve("target");
            if (Files.isDirectory(targetDir)) {
                list(targetDir)
                    .filter(CmdLauncherClasspath::isJarFile)
                    .map(PathUtil::getName)
                    .map(CmdLauncherClasspath::stripVersion)
                    .forEach(name -> {
                        if (cpPathByName.containsKey(name)) {
                            cpPathByName.remove(name);
                            addIfExists(result, targetDir.resolve("classes"));
                            addIfExists(result, targetDir.resolve("test-classes"));
                        }
                    });
            }
        });
        // Maven dependencies for which are not part of workspace
        cpPathByName.values().forEach(result::add);

        // Launcher jar
        String launcherJar = PathManager.getJarPathForClass(CmdLauncherClasspath.class);
        result.add(Paths.get(launcherJar));

        return result;
    }

    private static void addIfExists(Set<Path> result, Path path) {
        if (Files.exists(path)) {
            result.add(path);
        }
    }

    private static String stripVersion(String name) {
        if (name.endsWith(".jar")) {
            int rIdx = name.length() - ".jar".length();
            if (name.endsWith("-SNAPSHOT.jar")) {
                rIdx = name.length() - "-SNAPSHOT.jar".length();
            }
            int lIdx = name.lastIndexOf('-', rIdx);
            if (lIdx > 0) {
                if (isVersion(name.substring(lIdx + 1, rIdx))) {
                    return name.substring(0, lIdx) + ".jar";
                }
            }
        }
        return name;
    }

    private static boolean isVersion(String text) {
        return text.chars().allMatch(c -> Character.isDigit(c) || (c == '.'));
    }

    private static Stream<Path> list(Path dir) {
        try {
            return Files.list(dir);
        } catch (IOException e) {
            LOGGER.warn("Failed to list directory: " + dir, e);
            return Stream.of();
        }
    }

    private static boolean isJarFile(Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".jar");
    }

    private static Path[] getMavenClasspath(Path moduleDir, Consumer<String> out, Consumer<String> err) {
        String outputFileName = "cp.txt";
        Path outputPath = moduleDir.resolve(outputFileName);

        String[] cmd = { "mvn.cmd", "dependency:build-classpath", "-Dmdep.outputFile=" + outputFileName };
        try {
            Process process = ProcessUtil.exec(cmd, moduleDir.toFile(), out, err);
            process.waitFor();

            String text = new String(Files.readAllBytes(outputPath));
            String[] cp = text.split(";");
            return Arrays.stream(cp)
                    .map(Paths::get)
                    .collect(Collectors.toList())
                    .toArray(new Path[0]);

        } catch (IOException | InterruptedException e) {
            out.accept("Running mvn dependency:build-classpath failed.\n" + e.toString());
        } finally {
            FileUtil.deleteFile(outputPath);
        }
        return new Path[0];
    }

}
