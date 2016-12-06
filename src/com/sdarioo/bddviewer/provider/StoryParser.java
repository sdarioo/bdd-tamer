package com.sdarioo.bddviewer.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class StoryParser {

    private static final Logger LOGGER = Logger.getInstance(StoryParser.class);

    private static final String SCENARIO_PREFIX = "Scenario:";
    private static final String META_PREFIX = "Meta:";
    private static final String EXAMPLES_PREFIX = "Examples:";

    public static Story parse(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        StoryBuilder builder = new StoryBuilder();
        builder.setLocation(new Location(path, 1, lines.size()));

        int scenarioStartLine = -1;
        List<String> scenarioLines = new ArrayList<>();
        for (int i = 1; i <= lines.size(); i++) {
            String line = lines.get(i - 1).trim();

            if (line.startsWith(SCENARIO_PREFIX)) {
                if (scenarioStartLine > 0) {
                    builder.addScenario(parseScenario(scenarioLines, path, scenarioStartLine));
                    scenarioLines.clear();
                }
                scenarioStartLine = i;
                scenarioLines.add(line);
                continue;
            }
            if (scenarioStartLine > 0) {
                scenarioLines.add(line);
            }
        }
        if (scenarioStartLine > 0) {
            builder.addScenario(parseScenario(scenarioLines, path, scenarioStartLine));
        }

        return builder.build();
    }


    private static Scenario parseScenario(List<String> scenarioLines, Path path, int lineNumber) {

        Location location = new Location(path, lineNumber, lineNumber + scenarioLines.size() - 1);
        ScenarioBuilder scenarioBuilder = new ScenarioBuilder();
        scenarioBuilder.setLocation(location);

        String header = scenarioLines.get(0);
        if (header.startsWith(SCENARIO_PREFIX)) {
            scenarioBuilder.setName(header.substring(SCENARIO_PREFIX.length()).trim());
        }
        boolean isExamples = false;
        StepBuilder currentStep = null;

        for (int i = 1; i < scenarioLines.size(); i++) {
            String line = scenarioLines.get(i);
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith(META_PREFIX)) {
                scenarioBuilder.setMeta(new Meta());
                continue;
            }
            if (line.startsWith("@") && (scenarioBuilder.getMeta() != null)) {
                int index = line.indexOf(' ', 1);
                if (index > 1) {
                    scenarioBuilder.getMeta().add(line.substring(1, index), line.substring(index + 1));
                } else {
                    scenarioBuilder.getMeta().add(line.substring(1), "");
                }
                continue;
            }
            if (line.startsWith(EXAMPLES_PREFIX)) {
                scenarioBuilder.setExamplesLocation(new Location(path, lineNumber + i + 1));
                isExamples = true;
                continue;
            }
            if (line.startsWith("|")) {
                if (!line.startsWith("|--")) {
                    if (isExamples) {
                        scenarioBuilder.addExamples(line);
                    } else if (currentStep != null) {
                        currentStep.addValues(line);
                    }
                }
            } else {
                if (currentStep != null) {
                    scenarioBuilder.addStep(currentStep.build());
                }
                currentStep = new StepBuilder(line);
                currentStep.setLocation(new Location(path, lineNumber + i));
            }
        }
        if (currentStep != null) {
            scenarioBuilder.addStep(currentStep.build());
        }

        return scenarioBuilder.build();
    }

}
