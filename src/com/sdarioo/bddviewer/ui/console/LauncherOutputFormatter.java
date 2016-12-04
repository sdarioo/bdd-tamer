package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.sdarioo.bddviewer.launcher.LauncherListener;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Step;
import com.sdarioo.bddviewer.ui.util.IdeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LauncherOutputFormatter implements LauncherListener {

    private boolean hideOutput;
    private FormatterMode formatterMode = FormatterMode.Normal;

    private Scenario currentScenario;
    private StepOutput currentStep;
    private int exampleCounter;
    private final List<StepOutput> bufferedSteps = new ArrayList<>();

    private final Console console;

    public LauncherOutputFormatter(Console console) {
        this.console = console;
    }

    public FormatterMode getFormatterMode() {
        return formatterMode;
    }

    public void setFormatterMode(FormatterMode formatterMode) {
        this.formatterMode = formatterMode;
    }

    @Override
    public void sessionStarted(List<Scenario> scope) {
        console.clear();
    }

    @Override
    public void sessionFinished() {
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
    public void outputLine(String line) {
        if (isLoggerError(line)) {
            errorLine(line);
            return;
        }
        if (currentScenario == null) {
            if (formatterMode == FormatterMode.Full) {
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
            stepFinished();

            List<Step> steps = currentScenario.getSteps();
            Step step = (currentStep == null) ?
                    steps.get(0) :
                    steps.get(steps.indexOf(currentStep.step) + 1);
            currentStep = new StepOutput(step, line);

        } else if (isStepValue(line)) {
            // Step value line e.g '| value1 | value2 |'
            currentStep.values.add(line);

        } else if (currentStep != null) {
            // Other line within step e.g logger
            currentStep.lines.add(line);
        }
        if ((currentStep != null) && (statusHolder.get() != null)) {
            currentStep.status = statusHolder.get();
        }
    }

    @Override
    public void errorLine(String line) {
        if (currentStep != null) {
            currentStep.errors.add(line);
        } else {
            console.println(line, Console.ContentType.ERROR);
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
            if (formatterMode == FormatterMode.Compact) {
                bufferedSteps.add(currentStep);
            } else {
                println(currentStep);
            }
            currentStep = null;
        }
    }

    private void println(StepOutput output) {
        boolean hideValues = output.hasValues() &&
                (formatterMode.value < FormatterMode.Extended.value);

        String[] stepText = splitFirstToken(output.lines.get(0));
        console.print(stepText[0] + ' ', Console.FontStyle.BOLD, JBColor.ORANGE);
        console.print(stepText[1]);
        if (hideValues) {
            console.print(" [...]", null, JBColor.GRAY);
        }
        console.print(' ' + output.status.text + ' ', Console.FontStyle.BOLD, output.status.color);
        console.printHyperlink(">>", gotoAction(output.step.getLocation()));
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
        if ((formatterMode == FormatterMode.Compact) && (status != Status.FAILED)) {
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
