package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLauncher.class);


    private Process runningProcess;

    @Override
    public void terminate() {
        if ((runningProcess != null) && runningProcess.isAlive()) {
            runningProcess.destroyForcibly();
        }
    }

    @Override
    protected TestResult execute(Scenario scenario) {

        Path path = null;
        try {
            Path runDir = getRunDirectory(scenario);
            if (runDir == null) {
                return new TestResult(RunStatus.Failed, 0L, "Cannot find run directory for scenario: " + scenario.getLocation().getPath());
            }
            path = writeToTempFile(scenario);
            String cmdLine = createCommandLine(path);
            Consumer<String> output = line -> notifyOutputLine(line);

            long startTime = System.currentTimeMillis();
            output.accept("Running: " + cmdLine);
            int exitCode = execute(cmdLine, runDir, output, output);
            long time = System.currentTimeMillis() - startTime;

            RunStatus status = (exitCode == 0) ? RunStatus.Passed : RunStatus.Failed;
            return new TestResult(status, time, "Process finished with exit code: " + exitCode);
        } catch (IOException e) {
            return new TestResult(RunStatus.Failed, 0L, e.toString());
        } finally {
            runningProcess = null;
            deleteFile(path);
        }
    }

    private int execute(String command, Path runDir, Consumer<String> out, Consumer<String> err) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runningProcess = runtime.exec(command, null, runDir.toFile());

            readStream(runningProcess.getInputStream(), out);
            readStream(runningProcess.getErrorStream(), err);
            return runningProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            err.accept(e.toString());
            return -1;
        }
    }

    private static void readStream(InputStream inputStream, Consumer<String> consumer) throws IOException {
        String line = null;
        try (BufferedReader stdError = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = stdError.readLine()) != null) {
                consumer.accept(line);
            }
        }
    }

    private static Path getRunDirectory(Scenario scenario) {
        Path path = scenario.getLocation().getPath();
        while (path != null) {
            // TODO - use scenario parent module
            if (Files.isDirectory(path) && "pscm_bdd".equals(path.getFileName().toString())) {
                return path;
            }
            path = path.getParent();
        }
        return null;
    }

    private static String createCommandLine(Path path) {
        String normalizedPath = path.toString().replace("\\", "/");
        normalizedPath = normalizedPath.replace(" ", "\\ ");

        List<String> argsList = new ArrayList<>();
        argsList.add("launchCustomScenario");
        argsList.add(normalizedPath);
        String args = argsList.stream().collect(Collectors.joining(" "));
        return "mvn.cmd exec:java -Dexec.classpathScope=test -Dexec.args=\"" + args + "\"";
    }

    private static Path writeToTempFile(Scenario scenario) throws IOException {
        Location location = scenario.getLocation();
        List<String> allLines = Files.readAllLines(location.getPath());
        List<String> scenarioLines = allLines.subList(location.getStartLine() - 1, location.getEndLine());

        Path path = File.createTempFile("bdd_scenario", ".txt").toPath();
        Files.write(path, scenarioLines);
        return path;
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
}
