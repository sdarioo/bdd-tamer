package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLauncher.class);

    @Override
    protected TestResult execute(Scenario scenario) {

        Path path = null;
        try {
            path = writeToTempFile(scenario);
            StringBuilder sb = new StringBuilder();
            execute(new String[] { "java", "-version"}, s->sb.append(s+'\n'), s->sb.append(s+'\n'));

            return new TestResult(RunStatus.Passed, 100L, path.toString() + '\n' + sb.toString());
        } catch (IOException e) {
            return new TestResult(RunStatus.Failed, 0L, e.toString());
        } finally {
            delete(path);
        }
    }

    private static final Path writeToTempFile(Scenario scenario) throws IOException {
        Location location = scenario.getLocation();
        List<String> allLines = Files.readAllLines(location.getPath());
        List<String> scenarioLines = allLines.subList(location.getStartLine() - 1, location.getEndLine());

        Path path = File.createTempFile("bdd_scenario", ".txt").toPath();
        Files.write(path, scenarioLines);
        return path;
    }

    private static void delete(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOGGER.warn(e.toString());
            }
        }
    }

    private static int execute(String[] command, Consumer<String> out, Consumer<String> err) {
        try {
            Process proc = Runtime.getRuntime().exec(command);

            String line;
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                while ((line = stdInput.readLine()) != null) {
                    out.accept(line);
                }
            }
            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
                while ((line = stdError.readLine()) != null) {
                    err.accept(line);
                }
            }
            return proc.waitFor();

        } catch (IOException | InterruptedException e) {
            err.accept(e.toString());
            return -1;
        }
    }

}
