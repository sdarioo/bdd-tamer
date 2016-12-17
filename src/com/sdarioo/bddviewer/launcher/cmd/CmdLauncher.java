package com.sdarioo.bddviewer.launcher.cmd;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.launcher.AbstractLauncher;
import com.sdarioo.bddviewer.launcher.LauncherOutputFormatter;
import com.sdarioo.bddviewer.launcher.RunStatus;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.launcher.cmd.app.Main;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.ConsoleProvider;
import com.sdarioo.bddviewer.ui.console.Console;
import com.sdarioo.bddviewer.util.FileUtil;
import com.sdarioo.bddviewer.util.ProcessUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = Logger.getInstance(CmdLauncher.class);

    private static final String PROCESS_ID = "BddLauncher";
    private static final String STORY_ARG = Main.STORY_ARG;
    private static final String REPORT_ARG = Main.REPORT_ARG;

    private static final String SCENARIO_PREFIX = "Scenario: ";
    private static final String AFTER_STORIES_LINE = "(AfterStories)";
    private static final String FAILED = "(FAILED)";
    private static final String NOT_PERFORMED = "(NOT PERFORMED)";

    private Process runningProcess;

    public CmdLauncher(Project project, ConsoleProvider console) {
        super(project, console);
    }

    @Override
    protected LauncherOutputFormatter createOutputFormatter(Console console) {
        return new CmdLauncherOutputFormatter(console);
    }

    @Override
    public void terminate() {
        if ((runningProcess != null) && runningProcess.isAlive()) {
            String msg = "Killing launcher process...";
            LOGGER.info(msg);
            notifyInfo(msg);
            ProcessUtil.kill(runningProcess, PROCESS_ID);
        }
    }

    @Override
    protected void executeAll(List<Scenario> scenarios) {
        Path tempWorkspace = null;
        LaunchMonitor monitor = new LaunchMonitor(scenarios);
        try {
            tempWorkspace = Files.createTempDirectory(PROCESS_ID);
            List<Path> tempStoryFiles = createTempStoryFiles(scenarios, tempWorkspace);

            Path scenariosModuleDir = findModuleDir(scenarios);
            if (scenariosModuleDir == null) {
                String msg = "Cannot determine bdd module directory. Launch aborted.";
                LOGGER.error(msg);
                notifyError(msg);
                return;
            }

            Path reportsDir = tempWorkspace.resolve("reports");
            String[] cmdLine = createCmdLine(scenariosModuleDir, reportsDir, tempStoryFiles);
            notifyInfo("Starting launcher process...");

            int exitCode = runCmdLine(cmdLine, line -> processOutputLine(line, monitor), this::notifyError);
            notifyInfo("Launcher process finished with exit code: " + exitCode);
        } catch (Throwable e) {
            String msg = "Execution failed: " + e.toString();
            LOGGER.error(msg, e);
            notifyError(msg);
            terminate();
        } finally {
            monitor.notifySkipped();
            FileUtil.deleteDir(tempWorkspace);
        }
    }

    private void processOutputLine(String line, LaunchMonitor monitor) {
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
        notifyOutput(line);
    }

    private int runCmdLine(String[] command, Consumer<String> out, Consumer<String> err) {
        try {
            runningProcess = ProcessUtil.exec(command, null, out, err);
            return runningProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            out.accept(e.toString());
            terminate();
            return -1;
        } finally {
            runningProcess = null;
        }
    }

    private String[] createCmdLine(Path scenariosModuleDir,
                                   Path reportsDir, List<Path> tempStoryFiles) throws IOException {

        notifyInfo("Building launcher classpath. This may take some time...");
        Set<Path> cp = CmdLauncherClasspath.buildClasspath(scenariosModuleDir, this::notifyOutput, this::notifyError);

        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-cp");
        cmd.add(cp.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
        cmd.add("-D" + PROCESS_ID); // used only for killing purpose
        cmd.add(Main.class.getName());

        cmd.add(normalizePath(scenariosModuleDir));
        cmd.add(REPORT_ARG + normalizePath(reportsDir));
        tempStoryFiles.forEach(p -> cmd.add(STORY_ARG + normalizePath(p)));
        return cmd.toArray(new String[0]);
    }

    private static String normalizePath(Path path) {
        String result = path.toString().replace("\\", "/");
        return result.replace(" ", "\\ ");
    }

    private static List<Path> createTempStoryFiles(List<Scenario> scenarios, Path parentDir) throws IOException {
        Set<Path> result = new LinkedHashSet<>();
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
        return new ArrayList<>(result);
    }

    private static Path findModuleDir(List<Scenario> scenarios) {
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

    private class LaunchMonitor {

        private final List<Scenario> scenarios;
        private final Set<Scenario> started = new HashSet<>();
        private final Set<Scenario> finished = new HashSet<>();

        private long startTime;
        private int scenarioIndex;
        private RunStatus currentStatus;
        private Scenario currentScenario;

        LaunchMonitor(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }

        void scenarioStarted(String name) {
            currentStatus = null;
            startTime = System.currentTimeMillis();

            currentScenario = scenarios.get(scenarioIndex);
            scenarioIndex += 1;

            if (!currentScenario.getName().equals(name)) {
                // Is it even possible?
                LOGGER.warn("Unexpected run order deteced!");
                currentScenario = scenarios.stream().filter(s -> name.equals(s.getName())).findFirst().get();
            }
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
                    notifyOutput(SCENARIO_PREFIX + s.getName());
                    notifyOutput(s.getSteps().get(0).getText() + ' ' + NOT_PERFORMED);
                    notifyTestFinished(s, TestResult.skipped(s));
                }
            });
        }

    }
}
