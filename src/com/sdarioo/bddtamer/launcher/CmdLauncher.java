package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Location;
import com.sdarioo.bddtamer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CmdLauncher extends AbstractLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLauncher.class);

    @Override
    protected TestResult execute(Scenario scenario) {

        Path path = null;
        try {
            path = writeToTempFile(scenario);

            return new TestResult(RunStatus.Passed, 100L, path.toString());
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

}
