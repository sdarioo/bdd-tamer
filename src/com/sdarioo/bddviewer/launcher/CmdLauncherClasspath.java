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
                                           Consumer<String> err) throws IOException {
        Set<Path> result = new LinkedHashSet<>();

        Path[] cp = getMavenClasspath(moduleDir, out, err);

        Map<String, Path> cpPathByName = new HashMap<>();
        for (Path path : cp) {
            String name = stripVersion(PathUtil.getName(path));
            cpPathByName.put(name.toLowerCase(), path);
        }

        // Module classes first
        result.add(moduleDir.resolve("target\\classes"));
        result.add(moduleDir.resolve("target\\test-classes"));

        // Classes and test classes from parent project
        Path rootDir = moduleDir.getParent();
        list(rootDir).forEach(path -> {
            if (path.getFileName().endsWith("cm-control-panel")) {
                // TODO - this should be detected automatically
                return;
            }
            Path targetDir = path.resolve("target");
            if (Files.isDirectory(targetDir)) {
                list(targetDir).stream()
                    .filter(CmdLauncherClasspath::isJarFile)
                    .map(PathUtil::getName)
                    .map(CmdLauncherClasspath::stripVersion)
                    .map(String::toLowerCase)
                    .forEach(name -> {
                        if (cpPathByName.containsKey(name)) {
                            cpPathByName.remove(name);
                            addIfExists(result, targetDir.resolve("classes"));
                            addIfExists(result, targetDir.resolve("test-classes"));
                        }
                    });
            }
        });
        // Maven dependencies for which we have no sources in project
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

    protected static String stripVersion(String name) {
        String ext = ".jar";
        if (!name.endsWith(ext)) {
            return name;
        }
        name = name.substring(0, name.length() - ext.length());
        return Arrays.stream(name.split("-"))
                .filter(n -> !isVersion(n))
                .filter(n -> !"SNAPSHOT".equals(n))
                .collect(Collectors.joining("-")) + ext;
    }

    private static boolean isVersion(String text) {
        return text.chars().allMatch(c -> Character.isDigit(c) || (c == '.'));
    }

    private static List<Path> list(Path dir) {
        try (Stream<Path> stream = Files.list(dir)){
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.warn("Failed to list directory: " + dir, e);
            return Collections.emptyList();
        }
    }

    private static boolean isJarFile(Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".jar");
    }

    private static Path[] getMavenClasspath(Path moduleDir,
                                            Consumer<String> out, Consumer<String> err) throws IOException {
        String outputFileName = "cp.txt";
        Path outputPath = moduleDir.resolve(outputFileName);

        String[] cmd = { "mvn.cmd", "dependency:build-classpath", "-DincludeScope=test",
                "-Dmdep.outputFile=" + outputFileName };

        consoleLog("Running cmd: " + Arrays.stream(cmd).collect(Collectors.joining(" ")) + " in: " + moduleDir, out);

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
            consoleLog("Running mvn dependency:build-classpath failed.\n" + e.toString(), err);
            throw (e instanceof IOException) ? (IOException)e : new IOException(e);
        } finally {
            FileUtil.deleteFile(outputPath);
        }
    }

    private static void consoleLog(String msg, Consumer<String> console) {
        LOGGER.debug(msg);
        console.accept(msg);
    }
}
