package com.sdarioo.bddviewer.launcher.cmd;

import com.sdarioo.bddviewer.launcher.LauncherOutputFormatter;
import com.sdarioo.bddviewer.launcher.SessionContext;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.*;
import com.sdarioo.bddviewer.ui.console.Console;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CmdLauncherOutputFormatterTest {

    private static final Step STEP1 = newStep("Given input1 with values", 1);
    private static final Step STEP2 = newStep("Given input2 with values", 2);
    private static final Step STEP3 = newStep("Then there is nothing left", 0);

    private static final Scenario SCENARIO = new ScenarioBuilder().setName("Test scenario")
                .addExamples("|example1|")
                .addExamples("|example2|")
                .setExamplesLocation(new Location(Paths.get("/"), 1))
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

        CmdLauncherOutputFormatter formatter = new UnderTest(CONSOLE);
        formatter.setFormatterMode(CmdLauncherOutputFormatter.FormatterMode.Full);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("[INFO] running stories\n" +
                "(BeforeStory)\n" +
                "Scenario: Test scenario\n" +
                "Meta: \n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1} >>\n" +
                "Given input1 with values (PASSED) >>\n" +
                "|v1|\n" +
                "Given input2 with values (PASSED) >>\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (PASSED) >>\n" +
                "Example: {value=2} >>\n" +
                "Given input1 with values (PASSED) >>\n" +
                "|v1|\n" +
                "Given input2 with values (FAILED) >>\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (NOT PERFORMED) >>\n" +
                "[DEBUG] stopping\n" +
                "(AfterStory)\n", text);
    }

    @Test
    public void testExtendedMode() {
        CmdLauncherOutputFormatter formatter = new UnderTest(CONSOLE);
        formatter.setFormatterMode(CmdLauncherOutputFormatter.FormatterMode.Extended);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta: \n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1} >>\n" +
                "Given input1 with values (PASSED) >>\n" +
                "|v1|\n" +
                "Given input2 with values (PASSED) >>\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (PASSED) >>\n" +
                "Example: {value=2} >>\n" +
                "Given input1 with values (PASSED) >>\n" +
                "|v1|\n" +
                "Given input2 with values (FAILED) >>\n" +
                "|v1|\n" +
                "|v2|\n" +
                "Then there is nothing left (NOT PERFORMED) >>\n", text);
    }

    @Test
    public void testNormalMode() {
        CmdLauncherOutputFormatter formatter = new UnderTest(CONSOLE);
        formatter.setFormatterMode(CmdLauncherOutputFormatter.FormatterMode.Normal);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta: \n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1} >>\n" +
                "Given input1 with values [...] (PASSED) >>\n" +
                "Given input2 with values [...] (PASSED) >>\n" +
                "Then there is nothing left (PASSED) >>\n" +
                "Example: {value=2} >>\n" +
                "Given input1 with values [...] (PASSED) >>\n" +
                "Given input2 with values [...] (FAILED) >>\n" +
                "Then there is nothing left (NOT PERFORMED) >>\n", text);
    }

    @Test
    public void testCompactMode() {
        CmdLauncherOutputFormatter formatter = new UnderTest(CONSOLE);
        formatter.setFormatterMode(CmdLauncherOutputFormatter.FormatterMode.Compact);
        simulateSession(formatter);

        String text = CONSOLE.getContent();
        assertEquals("Scenario: Test scenario\n" +
                "Meta: \n" +
                "@Requirement A-1, B-2\n" +
                "@DomainObject Car\n" +
                "Example: {value=1} >>\n" +
                "[...] (PASSED)\n" +
                "Example: {value=2} >>\n" +
                "Given input1 with values [...] (PASSED) >>\n" +
                "Given input2 with values [...] (FAILED) >>\n" +
                "Then there is nothing left (NOT PERFORMED) >>\n", text);
    }

    private static void simulateSession(CmdLauncherOutputFormatter formatter) {
        SessionContext context = new SessionContext();
        formatter.sessionStarted(Collections.singletonList(SCENARIO), context);
        Arrays.asList(BEFORE_SCENARIO_OUTPUT).forEach(l -> formatter.outputLine(l, LauncherOutputFormatter.Severity.Normal));

        formatter.scenarioStarted(SCENARIO);
        Arrays.asList(SCENARIO_OUTPUT).forEach(l -> formatter.outputLine(l, LauncherOutputFormatter.Severity.Normal));
        formatter.scenarioFinished(SCENARIO, TestResult.skipped(SCENARIO));
        formatter.sessionFinished(context);

        Arrays.asList(AFTER_SCENARIO_OUTPUT).forEach(l -> formatter.outputLine(l, LauncherOutputFormatter.Severity.Normal));
    }

    private static Step newStep(String text, int valuesCount) {
        StepBuilder builder = new StepBuilder(text);
        for (int i = 0; i < valuesCount; i++) {
            builder.addValues(String.format("|v%d|", i));
        }
        return builder.build();
    }

    private static class UnderTest extends CmdLauncherOutputFormatter {

        private final Console console;
        public UnderTest(Console console) {
            super(console);
            this.console = console;
        }

        @Override
        public void sessionStarted(List<Scenario> scope, SessionContext context) {
            // Suppress console header (time dependant)
            console.clear();
        }

        @Override
        public void sessionFinished(SessionContext context) {
            // Suppress console footer (time dependant)
        }
    }
}