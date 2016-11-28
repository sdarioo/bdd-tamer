package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLauncher.class);
    private static final String SCENARIOS_ARG = "--scenarios";
    private static final String RUNNING_STORY_PREFIX = "Running story ";
    private static final String GENERATING_REPORT_PREFIX = "Generating reports view to ";


    private Process runningProcess;

    @Override
    public void terminate() {
        if ((runningProcess != null) && runningProcess.isAlive()) {
            LOGGER.info("Terminating launcher process...");
            runningProcess.destroyForcibly();
        }
    }

    @Override
    protected void executeAll(List<Scenario> scenarios) {
        List<Path> paths = null;
        Path configPath = null;
        try {
            paths = createTempFiles(scenarios);
            configPath = createConfigPath(paths);

            Path runDir = getRunDirectory(scenarios);
            String cmdLine = createCommandLine(configPath);
            notifyOutputLine("Running CmdLine: " + cmdLine);

            LaunchMonitor monitor = new LaunchMonitor(scenarios, paths);
            int exitCode = runCmdLine(cmdLine, runDir, line -> processOutputLine(line, monitor));
            notifyOutputLine("Process finished with exit code: " + exitCode);
        } catch (IOException e) {
            LOGGER.error("Execution failed", e);
        } finally {
            deleteFiles(paths);
            deleteFile(configPath);
        }
    }

    private void processOutputLine(String line, LaunchMonitor monitor) {
        notifyOutputLine(line);
        if (line.trim().startsWith(RUNNING_STORY_PREFIX)) {
            String path = line.substring(RUNNING_STORY_PREFIX.length());
            monitor.scenarioStarted(Paths.get(path));
        }
        if (line.trim().startsWith(GENERATING_REPORT_PREFIX)) {
            int pathStart = line.indexOf('\'');
            int pathEnd = line.indexOf('\'', pathStart + 1);
            String reportsDir = line.substring(pathStart + 1, pathEnd);

            RunStatus status = readStatusFromReportFile(Paths.get(reportsDir), monitor.currentPath);
            monitor.scenarioFinished(status);
        }
    }

    private int runCmdLine(String command, Path runDir, Consumer<String> out) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runningProcess = runtime.exec(command, null, runDir.toFile());

            readStream(runningProcess.getInputStream(), out);
            readStream(runningProcess.getErrorStream(), out);
            return runningProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            out.accept(e.toString());
            terminate();
            return -1;
        } finally {
            runningProcess = null;
        }
    }

    private static void readStream(InputStream inputStream, Consumer<String> consumer) throws IOException {
        String line;
        try (BufferedReader stdError = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = stdError.readLine()) != null) {
                consumer.accept(line);
            }
        }
    }

    private static Path getRunDirectory(List<Scenario> scenarios) {
        Scenario scenario = scenarios.get(0);
        Path path = scenario.getLocation().getPath();
        while (path != null) {
            if (Files.isDirectory(path) && Files.isRegularFile(path.resolve("pom.xml"))) {
                return path;
            }
            path = path.getParent();
        }
        return null;
    }

    private static String createCommandLine(Path configPath) {
        List<String> argsList = new ArrayList<>();
        argsList.add("launchCustomScenario");
        argsList.add(SCENARIOS_ARG + '=' + normalizePath(configPath));
        String args = argsList.stream().collect(Collectors.joining(" "));
        return "mvn.cmd exec:java -Dexec.classpathScope=test -Dexec.args=\"" + args + "\"";
    }

    private static String normalizePath(Path path) {
        String result = path.toString().replace("\\", "/");
        return result.replace(" ", "\\ ");
    }

    private static Path createConfigPath(List<Path> paths) throws IOException {
        Path config = File.createTempFile("config", ".story").toPath();
        Files.write(config, paths.stream().map(Object::toString).collect(Collectors.toList()));
        return config;
    }

    private static List<Path> createTempFiles(List<Scenario> scenarios) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            result.add(createTempFile(scenario));
        }
        return result;
    }

    private static Path createTempFile(Scenario scenario) throws IOException {
        Location location = scenario.getLocation();
        List<String> allLines = Files.readAllLines(location.getPath());
        List<String> scenarioLines = allLines.subList(location.getStartLine() - 1, location.getEndLine());

        String name = PathUtil.getNameWithoutExtension(scenario.getLocation().getPath());
        Path path = File.createTempFile(name, ".story").toPath();
        Files.write(path, scenarioLines);
        return path;
    }

    private static void deleteFiles(List<Path> paths) {
        if (paths != null) {
            paths.forEach(CmdLauncher::deleteFile);
        }
    }

    private static void deleteFile(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOGGER.warn(e.toString());
            }
        }
    }

    private static RunStatus readStatusFromReportFile(Path reportDirPath, Path scenarioPath) {
        Path reportPath = reportDirPath.resolve(scenarioPath.getFileName() + ".stats");
        try {
            List<String> lines = Files.readAllLines(reportPath);
            boolean success = lines.stream().map(String::trim).filter(l -> l.equals("scenariosSuccessful=1")).findFirst().isPresent();
            return success ? RunStatus.Passed : RunStatus.Failed;
        } catch (IOException e) {
            LOGGER.error("Error while reading status file.", e);
            return RunStatus.Failed;
        }
    }

    private class LaunchMonitor {
        private final Map<Path, Scenario> scenarioByPath = new HashMap<>();
        private Path currentPath;
        private Scenario currentScenario;
        private long startTime;

        LaunchMonitor(List<Scenario> scenarios, List<Path> paths) {
            for (int i = 0; i < scenarios.size(); i++) {
                scenarioByPath.put(paths.get(i), scenarios.get(i));
            }
        }

        void scenarioStarted(Path path) {
            currentPath = path;
            currentScenario = scenarioByPath.get(path);
            startTime = System.currentTimeMillis();
            notifyTestStarted(currentScenario);
        }

        void scenarioFinished(RunStatus status) {
            long time = System.currentTimeMillis() - startTime;
            notifyTestFinished(currentScenario, new TestResult(status, time, ""));

            currentPath = null;
            currentScenario = null;
        }

    }
}
