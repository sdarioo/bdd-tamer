package com.sdarioo.bddviewer.ui.console;

import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class LauncherOutputFormatterTest {

    private static final Step STEP1 = newStep("Given input1 with values", 1);
    private static final Step STEP2 = newStep("Given input2 with values", 2);
    private static final Step STEP3 = newStep("Then there is nothing left", 0);

    private static final Scenario SCENARIO = new ScenarioBuilder().setName("Test scenario")
                .addStep(STEP1)
                .addStep(STEP2)
                .addStep(STEP3).build();

    private static final String[] BEFORE_SCENARIO_OUTPUT = {
            "[INFO] running stories",
            "(BeforeStory)",
    };
    private static final String[] AFTER_SCENARIO_OUTPUT = {
            "[DEBUG] stopping",
            "(AfterStory)",
    };
    private static final String[] SCENARIO_OUTPUT = {
            "Scenario: " + SCENARIO.getName(),
            "Meta:",
            "@Requirement A-1, B-2",
            "@DomainObject Car",
            "Examples:",
            "|example1|",
            "|example2|",
            "Example: {value=1}", // example-1
            STEP1.getText(),
            "|v1|",
            STEP2.getText(),
            "|v1|",
            "|v2|",
            STEP3.getText(),
            "Example: {value=2}", // example-2
            STEP1.getText(),
            "|v1|",
            STEP2.getText(),
            "|v1|",
            "|v2| (FAILED)",
            STEP3.getText() + " (NOT PERFORMED)",

    };

    private static final ConsoleMock CONSOLE = new ConsoleMock();

    @Test
    public void testFullMode() {

        LauncherOutputFormatter formatter = new LauncherOutputFormatter(CONSOLE);
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Full);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("[INFO] running stories\n" +
                "(BeforeStory)\n" +
                "Scenario: Test scenario\n" +
                "Meta:\n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1}\n" +
                "Given input1 with values (PASSED)\n" +
                "|v1|\n" +
                "Given input2 with values (PASSED)\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (PASSED)\n" +
                "Example: {value=2}\n" +
                "Given input1 with values (PASSED)\n" +
                "|v1|\n" +
                "Given input2 with values (FAILED)\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (NOT PERFORMED)\n" +
                "[DEBUG] stopping\n" +
                "(AfterStory)\n", text);
    }

    @Test
    public void testExtendedMode() {
        LauncherOutputFormatter formatter = new LauncherOutputFormatter(CONSOLE);
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Extended);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta:\n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1}\n" +
                "Given input1 with values (PASSED)\n" +
                "|v1|\n" +
                "Given input2 with values (PASSED)\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (PASSED)\n" +
                "Example: {value=2}\n" +
                "Given input1 with values (PASSED)\n" +
                "|v1|\n" +
                "Given input2 with values (FAILED)\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (NOT PERFORMED)\n", text);
    }

    @Test
    public void testNormalMode() {
        LauncherOutputFormatter formatter = new LauncherOutputFormatter(CONSOLE);
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Normal);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta:\n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1}\n" +
                "Given input1 with values [...] (PASSED)\n" +
                "Given input2 with values [...] (PASSED)\n" +
                "Then there is nothing left (PASSED)\n" +
                "Example: {value=2}\n" +
                "Given input1 with values [...] (PASSED)\n" +
                "Given input2 with values [...] (FAILED)\n" +
                "Then there is nothing left (NOT PERFORMED)\n", text);
    }

    @Test
    public void testCompactMode() {
        LauncherOutputFormatter formatter = new LauncherOutputFormatter(CONSOLE);
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Compact);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta:\n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1}\n" +
                "[...] (PASSED)\n" +
                "Example: {value=2}\n" +
                "Given input1 with values [...] (PASSED)\n" +
                "Given input2 with values [...] (FAILED)\n" +
                "Then there is nothing left (NOT PERFORMED)\n", text);
    }

    private static void simulateSession(LauncherOutputFormatter formatter) {
        formatter.sessionStarted(Collections.singletonList(SCENARIO));
        Arrays.asList(BEFORE_SCENARIO_OUTPUT).forEach(formatter::outputLine);

        formatter.scenarioStarted(SCENARIO);
        Arrays.asList(SCENARIO_OUTPUT).forEach(formatter::outputLine);
        formatter.scenarioFinished(SCENARIO, TestResult.skipped(SCENARIO));
        formatter.sessionFinished();

        Arrays.asList(AFTER_SCENARIO_OUTPUT).forEach(formatter::outputLine);
    }

    private static Step newStep(String text, int valuesCount) {
        Step step = new Step(text);
        for (int i = 0; i < valuesCount; i++) {
            step.getValues().add(Arrays.asList("v" + i));
        }
        return step;
    }
}