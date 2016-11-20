package com.sdarioo.bddviewer.provider;

import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Meta;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.ScenarioBuilder;
import com.sdarioo.bddviewer.model.Step;
import com.sdarioo.bddviewer.model.Story;
import com.sdarioo.bddviewer.model.StoryBuilder;
import com.sdarioo.bddviewer.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class StoryParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoryParser.class);

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
        ScenarioBuilder builder = new ScenarioBuilder();
        builder.setLocation(location);

        for (String line : scenarioLines) {
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith(SCENARIO_PREFIX)) {
                builder.setName(line.substring(SCENARIO_PREFIX.length()).trim());
                continue;
            }
            if (line.startsWith(META_PREFIX)) {
                builder.setMeta(new Meta());
                continue;
            }
            if (line.startsWith("@") && (builder.getMeta() != null)) {
                int index = line.indexOf(' ', 1);
                if (index > 1) {
                    builder.getMeta().add(line.substring(1, index), line.substring(index + 1));
                } else {
                    builder.getMeta().add(line.substring(1), "");
                }
                continue;
            }
            if (line.startsWith(EXAMPLES_PREFIX)) {
                builder.setExamples(new Table());
                continue;
            }
            if (line.startsWith("|")) {
                if (builder.getExamples() != null) {
                    builder.getExamples().add(line);
                } else if (builder.lastStep() != null) {
                    builder.lastStep().getValues().add(line);
                }
            } else {
                builder.addStep(new Step(line));
            }

        }
        return builder.build();
    }

}
