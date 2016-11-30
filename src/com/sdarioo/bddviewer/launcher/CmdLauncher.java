package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.util.FileUtil;
import com.sdarioo.bddviewer.util.PathUtil;
import com.sdarioo.bddviewer.util.ProcessUtil;
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
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLauncher.class);

    private static final String CMD = "mvn.cmd exec:java -Dexec.classpathScope=test -Dexec.args={0}";
    private static final String LAUNCH_ARG = "launchCustomScenario";
    private static final String SCENARIOS_ARG = "--scenarios";

    private static final String RUNNING_STORY_PREFIX = "Running story ";
    private static final String GENERATING_REPORT_PREFIX = "Generating reports view to ";

    private final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Launcher stream reader thread.");
            t.setDaemon(true);
            return t;
        }
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
        List<Path> paths = null;
        Path configPath = null;
        LaunchMonitor monitor = new LaunchMonitor(scenarios);
        try {
            paths = createTempFiles(scenarios);
            monitor.setScenarioPaths(paths);

            configPath = createConfigPath(paths);
            Path runDir = getRunDirectory(scenarios);
            if (runDir == null) {
                String message = "Cannot determine run directory. Launch aborted.";
                LOGGER.error(message);
                notifyOutputLine(message);
                return;
            }
            String cmdLine = createCommandLine(configPath);
            notifyOutputLine("Starting launcher process: " + cmdLine);

            int exitCode = runCmdLine(cmdLine, runDir, line -> processOutputLine(line, monitor));
            notifyOutputLine("Launcher process finished with exit code: " + exitCode);
        } catch (Throwable e) {
            LOGGER.error("Execution failed", e);
            terminate();
        } finally {
            monitor.notifySkipped();
            FileUtil.deleteFiles(paths);
            FileUtil.deleteFile(configPath);
            monitor.reportDirs.stream().forEach(FileUtil::deleteDir);
        }
    }

    private void processOutputLine(String line, LaunchMonitor monitor) {
        notifyOutputLine(line);
        if (line.trim().startsWith(RUNNING_STORY_PREFIX)) {
            String path = line.substring(RUNNING_STORY_PREFIX.length());
            monitor.scenarioStarted(Paths.get(path));
        }
        if (line.trim().startsWith(GENERATING_REPORT_PREFIX)) {
            int pathStartIdx = line.indexOf('\'') + 1;
            int pathEndIdx = line.indexOf('\'', pathStartIdx);
            String reportsDir = line.substring(pathStartIdx, pathEndIdx);

            Path reportsDirPath = Paths.get(reportsDir);
            RunStatus status = readStatusFromReportFile(reportsDirPath, monitor.currentScenarioPath);

            monitor.reportDirs.add(reportsDirPath);
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

    private void readStream(InputStream inputStream, Consumer<String> consumer) {
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

    private static String createCommandLine(Path configPath) {
        List<String> argsList = new ArrayList<>();
        argsList.add(LAUNCH_ARG);
        argsList.add(SCENARIOS_ARG + '=' + normalizePath(configPath));
        String args = '"' + argsList.stream().collect(Collectors.joining(" ")) + '"';
        return MessageFormat.format(CMD, args);
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

        private final List<Scenario> scenarios;
        private final Set<Scenario> started = new HashSet<>();
        private final Set<Scenario> finished = new HashSet<>();

        private final Map<Path, Scenario> scenarioByPath = new HashMap<>();
        private final List<Path> reportDirs = new ArrayList<>();

        private Scenario currentScenario;
        private Path currentScenarioPath;
        private long startTime;


        LaunchMonitor(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }

        void setScenarioPaths(List<Path> paths) {
            for (int i = 0; i < scenarios.size(); i++) {
                scenarioByPath.put(paths.get(i), scenarios.get(i));
            }
        }

        void scenarioStarted(Path path) {
            currentScenarioPath = path;
            currentScenario = scenarioByPath.get(path);
            startTime = System.currentTimeMillis();
            notifyTestStarted(currentScenario);
            started.add(currentScenario);
        }

        void scenarioFinished(RunStatus status) {
            long time = System.currentTimeMillis() - startTime;
            notifyTestFinished(currentScenario, new TestResult(status, time, ""));
            finished.add(currentScenario);

            currentScenario = null;
            currentScenarioPath = null;
        }

        /**
         * Send 'skipped' notifications for all scenarios that has not been completed yet.
         */
        void notifySkipped() {
            scenarioByPath.values().stream().forEach(s -> {
                if (!started.contains(s)) {
                    notifyTestStarted(s);
                }
                if (!finished.contains(s)) {
                    notifyTestFinished(s, TestResult.skipped(s));
                }
            });
        }

    }
}
