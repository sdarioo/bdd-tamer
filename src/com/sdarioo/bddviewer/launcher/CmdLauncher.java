package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.util.FileUtil;
import com.sdarioo.bddviewer.util.ProcessUtil;
import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = Logger.getInstance(CmdLauncher.class);

    private static final String CMD = "mvn.cmd exec:java -Dexec.classpathScope=test -Dexec.args={0}";
    private static final String LAUNCH_ARG = "launchCustomScenario";
    private static final String SCENARIO_ARG = "--scenario";
    private static final String REPORT_ARG = "--report";

    private static final String SCENARIO_PREFIX = "Scenario: ";
    private static final String AFTER_STORIES_LINE = "(AfterStories)";
    private static final String FAILED = "(FAILED)";
    private static final String NOT_PERFORMED = "(NOT PERFORMED)";

    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "Launcher stream reader thread.");
        t.setDaemon(true);
        return t;
    });

    private Process runningProcess;

    @Override
    public void terminate() {
        if ((runningProcess != null) && runningProcess.isAlive()) {
            String message = "Killing launcher process...";
            LOGGER.info(message);
            notifyOutputLine(message);

            ProcessUtil.kill(runningProcess, LAUNCH_ARG);
        }
    }

    @Override
    protected void executeAll(List<Scenario> scenarios) {
        Path tempWorkspace = null;
        LaunchMonitor monitor = new LaunchMonitor(scenarios);
        try {
            tempWorkspace = Files.createTempDirectory("bdd_launcher");
            List<Path> paths = createTempStoryFiles(scenarios, tempWorkspace);

            Path runDir = getRunDirectory(scenarios);
            if (runDir == null) {
                String message = "Cannot determine run directory. Launch aborted.";
                LOGGER.error(message);
                notifyErrorLine(message);
                return;
            }
            String cmdLine = createCommandLine(tempWorkspace, paths);
            notifyOutputLine("Starting launcher process: " + cmdLine);

            int exitCode = runCmdLine(cmdLine, runDir, line -> notifyOutputLine(line, monitor), this::notifyErrorLine);
            notifyOutputLine("Launcher process finished with exit code: " + exitCode);
        } catch (Throwable e) {
            LOGGER.error("Execution failed", e);
            terminate();
        } finally {
            monitor.notifySkipped();
            FileUtil.deleteDir(tempWorkspace);
        }
    }

    private void notifyOutputLine(String line, LaunchMonitor monitor) {
        String trimmedLine = line.trim();

        if (trimmedLine.endsWith(FAILED)) {
            monitor.currentStatus = RunStatus.Failed;
        }
        if (trimmedLine.endsWith(NOT_PERFORMED) && (monitor.currentStatus == null)) {
            monitor.currentStatus = RunStatus.Skipped;
        }
        // scenario: name
        if (trimmedLine.startsWith(SCENARIO_PREFIX)) {
            if (monitor.currentScenario != null) {
                monitor.scenarioFinished();
            }
            String name = trimmedLine.substring(SCENARIO_PREFIX.length());
            monitor.scenarioStarted(name);
        }
        // (AfterStories)
        if (line.trim().equals(AFTER_STORIES_LINE)) {
            if (monitor.currentScenario != null) {
                monitor.scenarioFinished();
            }
        }
        notifyOutputLine(line);
    }

    private int runCmdLine(String command, Path runDir, Consumer<String> out, Consumer<String> err) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runningProcess = runtime.exec(command, null, runDir.toFile());

            readStreamAsync(runningProcess.getInputStream(), out);
            readStreamAsync(runningProcess.getErrorStream(), err);
            return runningProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            out.accept(e.toString());
            terminate();
            return -1;
        } finally {
            runningProcess = null;
        }
    }

    private void readStreamAsync(InputStream inputStream, Consumer<String> consumer) {
        executor.submit(() -> {
            String line;
            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = stdError.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                consumer.accept("Error reading process output: " + e.toString());
            }
        });
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

    private static String createCommandLine(Path tempWorkplace, List<Path> paths) {
        List<String> argsList = new ArrayList<>();
        argsList.add(LAUNCH_ARG);
        paths.forEach(p -> argsList.add(SCENARIO_ARG + '=' + normalizePath(p)));
        argsList.add(REPORT_ARG + '=' + normalizePath(tempWorkplace.resolve("reports")));
        String args = '"' + argsList.stream().collect(Collectors.joining(" ")) + '"';
        return MessageFormat.format(CMD, args);
    }

    private static String normalizePath(Path path) {
        String result = path.toString().replace("\\", "/");
        return result.replace(" ", "\\ ");
    }

    private static List<Path> createTempStoryFiles(List<Scenario> scenarios, Path parentDir) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            Path path = scenario.getLocation().getPath();
            Path tempPath = parentDir.resolve(path.getFileName());
            if (Files.isRegularFile(tempPath)) {
                Files.write(tempPath, scenario.readLines(), StandardOpenOption.APPEND);
            } else {
                Files.write(tempPath, scenario.readLines());
            }
            result.add(tempPath);
        }
        return result;
    }

    private class LaunchMonitor {

        private final List<Scenario> scenarios;
        private final Set<Scenario> started = new HashSet<>();
        private final Set<Scenario> finished = new HashSet<>();

        private long startTime;
        private RunStatus currentStatus;
        private Scenario currentScenario;

        LaunchMonitor(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }

        void scenarioStarted(String name) {
            currentStatus = null;
            startTime = System.currentTimeMillis();
            currentScenario = scenarios.stream().filter(s -> name.equals(s.getName())).findFirst().get();

            notifyTestStarted(currentScenario);
            started.add(currentScenario);
        }

        void scenarioFinished() {
            long time = System.currentTimeMillis() - startTime;
            if (currentStatus == null) {
                currentStatus = RunStatus.Passed;
            }
            notifyTestFinished(currentScenario, new TestResult(currentStatus, time));
            finished.add(currentScenario);

            currentScenario = null;
        }

        /**
         * Send 'skipped' notifications for all scenarios that has not been completed yet.
         */
        void notifySkipped() {
            scenarios.stream().forEach(s -> {
                if (!started.contains(s)) {
                    notifyTestStarted(s);
                }
                if (!finished.contains(s)) {
                    notifyOutputLine(SCENARIO_PREFIX + s.getName());
                    notifyOutputLine(s.getSteps().get(0).getText() + ' ' + NOT_PERFORMED);
                    notifyTestFinished(s, TestResult.skipped(s));
                }
            });
        }

    }
}
