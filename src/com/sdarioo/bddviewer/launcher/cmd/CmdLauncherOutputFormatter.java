package com.sdarioo.bddviewer.launcher.cmd;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.sdarioo.bddviewer.launcher.LauncherOutputFormatter;
import com.sdarioo.bddviewer.launcher.SessionContext;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Step;
import com.sdarioo.bddviewer.ui.console.Console;
import com.sdarioo.bddviewer.ui.console.actions.ShowDetailsAction;
import com.sdarioo.bddviewer.ui.util.IdeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CmdLauncherOutputFormatter implements LauncherOutputFormatter {

    private static final CmdLauncherOutputFormatter.FormatterMode DEFAULT_FORMAT_MODE =
            CmdLauncherOutputFormatter.FormatterMode.Compact;

    private FormatterMode formatterMode = DEFAULT_FORMAT_MODE;

    private boolean hideOutput;
    private Scenario currentScenario;
    private StepOutput currentStep;
    private int exampleCounter;
    private final List<StepOutput> bufferedSteps = new ArrayList<>();

    private final Console console;

    public CmdLauncherOutputFormatter(Console console) {
        this.console = console;
    }

    public void setFormatterMode(FormatterMode formatterMode) {
        this.formatterMode = formatterMode;
    }

    public FormatterMode getFormatterMode() {
        return console.isShowDetails() ? FormatterMode.Full : formatterMode;
    }

    @Override
    public void sessionStarted(List<Scenario> scope, SessionContext context) {
        console.clear();
        console.println("Session started at " + Instant.now(), Console.FontStyle.ITALIC, JBColor.GRAY);
        if (getFormatterMode() != FormatterMode.Full) {
            console.println(String.format("To see more console output press '%s' button.", ShowDetailsAction.TEXT),
                    Console.FontStyle.ITALIC, JBColor.GRAY);
        }
    }

    @Override
    public void sessionFinished(SessionContext context) {
        console.println("Session finished at " + Instant.now(), Console.FontStyle.ITALIC, JBColor.GRAY);
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        currentStep = null;
        exampleCounter = 0;
        currentScenario = scenario;
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
        stepFinished();
        flushBufferedSteps();
        currentScenario = null;
    }

    @Override
    public void outputLine(String line, Severity severity) {

        if (currentScenario == null) {
            if (isLoggerError(line) || (severity == Severity.Error)) {
                console.println(line, Console.ContentType.ERROR);
            } else if ((getFormatterMode() == FormatterMode.Full) || (severity == Severity.Info)) {
                console.println(line);
            }
            return;
        }
        if (line.startsWith("Scenario:")) {
            String[] pair = splitFirstToken(line);
            console.print(pair[0] + ' ', Console.FontStyle.BOLD, JBColor.ORANGE);
            console.printHyperlink(pair[1], gotoAction(currentScenario.getLocation()));
            console.println();
            return;
        }
        if (line.startsWith("Meta:") || line.startsWith("@")) {
            String[] pair = splitFirstToken(line);
            console.print(pair[0] + ' ', Console.FontStyle.BOLD, JBColor.ORANGE);
            console.println(pair[1]);
            return;
        }
        if (line.startsWith("Examples:")) {
            hideOutput = true;
            return;
        }
        if (line.startsWith("Example: ")) {
            hideOutput = false;
            stepFinished();
            flushBufferedSteps();
            exampleCounter += 1;
            console.print(line + ' ', Console.FontStyle.BOLD, JBColor.MAGENTA);
            Location startLocation = currentScenario.getExamples().getLocation();
            console.printHyperlink(">>", gotoAction(new Location(startLocation.getPath(), startLocation.getStartLine() + exampleCounter)));
            console.println();
            return;
        }
        if (hideOutput) {
            return;
        }
        AtomicReference<Status> statusHolder = new AtomicReference<>();
        line = trimStatus(line, statusHolder);

        if (isStep(line)) {
            List<Step> steps = currentScenario.getSteps();
            if (currentStep != null) {
                Step step = steps.get(steps.indexOf(currentStep.step) + 1);
                stepFinished();
                currentStep = new StepOutput(step, line);
            } else {
                currentStep = new StepOutput(steps.get(0), line);
            }
        } else if (isStepValue(line)) {
            // Step value line e.g '| value1 | value2 |'
            currentStep.values.add(line);

        } else if (currentStep != null) {
            // Other line within step e.g logger
            if (isLoggerError(line) || (severity == Severity.Error)) {
                currentStep.errors.add(line);
            } else {
                currentStep.lines.add(line);
            }
        }
        if ((currentStep != null) && (statusHolder.get() != null)) {
            currentStep.status = statusHolder.get();
        }
    }

    private static String trimStatus(String line, AtomicReference<Status> statusHolder) {
        for (Status status : Status.values()) {
            if (line.endsWith(status.text)) {
                statusHolder.set(status);
                return line.substring(0, line.length() - status.text.length()).trim();
            }
        }
        return line;
    }

    private void stepFinished() {
        if (currentStep != null) {
            if (getFormatterMode() == FormatterMode.Compact) {
                bufferedSteps.add(currentStep);
            } else {
                println(currentStep);
            }
            currentStep = null;
        }
    }

    private void println(StepOutput output) {
        boolean hideValues = output.hasValues() &&
                (getFormatterMode().value < FormatterMode.Extended.value);

        String[] stepText = splitFirstToken(output.lines.get(0));
        console.print(stepText[0] + ' ', Console.FontStyle.BOLD, JBColor.ORANGE);
        console.print(stepText[1]);
        if (hideValues) {
            console.print(" [...]", null, JBColor.GRAY);
        }
        console.print(' ' + output.status.text + ' ', Console.FontStyle.BOLD, output.status.color);
        Location location = output.step.getLocation();
        console.printHyperlink(">>", gotoAction(location));
        console.println();

        if (!hideValues) {
            output.values.forEach(line -> console.println(line, null, JBColor.GRAY));
        }
        output.lines.stream().skip(1).forEach(line -> console.println(line));
        output.errors.forEach(line -> console.println(line, Console.ContentType.ERROR));
    }

    private void flushBufferedSteps() {
        if (bufferedSteps.isEmpty()) {
            return;
        }
        Status status = getOverallStatus(bufferedSteps);
        if ((getFormatterMode() == FormatterMode.Compact) && (status != Status.FAILED)) {
            console.print("[...] ", null, JBColor.GRAY);
            console.println(status.text, Console.FontStyle.BOLD, status.color);
        } else {
            bufferedSteps.forEach(this::println);
        }
        bufferedSteps.clear();
    }

    private static Status getOverallStatus(List<StepOutput> outputs) {
        Set<Status> statuses = outputs.stream().map(s -> s.status).collect(Collectors.toSet());
        if (statuses.contains(Status.FAILED)) {
            return Status.FAILED;
        }
        if (statuses.contains(Status.NOT_PERFORMED)) {
            return Status.NOT_PERFORMED;
        }
        return Status.PASSED;
    }

    private static String[] splitFirstToken(String line) {
        int index = line.indexOf(' ');
        if (index > 0) {
            return new String[] { line.substring(0, index), line.substring(index + 1)};
        }
        return new String[] { line, ""};
    }

    private static Consumer<Project> gotoAction(Location location) {
        return project -> {
            IdeUtil.openInEditor(project, location);
        };
    }

    private static boolean isStep(String line) {
        return line.startsWith("Given ") ||
                line.startsWith("And ") ||
                line.startsWith("When ") ||
                line.startsWith("Then ");
    }

    private static boolean isStepValue(String line) {
        return line.trim().startsWith("|");
    }

    private static boolean isLoggerError(String line) {
        return line.contains("ERROR") && !line.contains("INFO");
    }

    public enum FormatterMode {
        Compact(0),
        Normal(1),
        Extended(2),
        Full(3);

        final int value;
        FormatterMode(int value) {
            this.value = value;
        }
    }

    enum Status {
        PASSED("(PASSED)", JBColor.GREEN),
        FAILED("(FAILED)", JBColor.RED),
        NOT_PERFORMED("(NOT PERFORMED)", JBColor.ORANGE);

        final String text;
        final JBColor color;
        Status(String text, JBColor color) {
            this.text = text;
            this.color = color;
        }
    }

    private static class StepOutput {
        final Step step;
        final List<String> lines = new ArrayList<>();
        final List<String> errors = new ArrayList<>();
        final List<String> values = new ArrayList<>();
        Status status = Status.PASSED;

        private StepOutput(Step step, String... lines) {
            this.step = step;
            this.lines.addAll(Arrays.asList(lines));
        }

        boolean hasValues() {
            return !values.isEmpty();
        }
    }

}
