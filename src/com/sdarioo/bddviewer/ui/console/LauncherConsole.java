package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.sdarioo.bddviewer.launcher.*;
import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Step;
import com.sdarioo.bddviewer.model.Table;
import com.sdarioo.bddviewer.ui.util.IdeUtil;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class LauncherConsole extends AbstractConsole implements LauncherListener {

    private boolean showLogs;
    private boolean showStepValues;

    private Scenario currentScenario;
    private Step currentStep;
    boolean isExamples;


    public LauncherConsole(Project project, SessionManager sessionManager) {
        super(project);
        sessionManager.getLauncher().addListener(this);
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean value) {
        showLogs = value;
    }

    public boolean isShowStepValues() {
        return showStepValues;
    }

    public void setShowStepValues(boolean showStepValues) {
        this.showStepValues = showStepValues;
    }

    @Override
    public void sessionStarted(List<Scenario> scope) {
        clear();
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        currentScenario = scenario;
        currentStep = null;
        isExamples = false;
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
        currentScenario = null;
        String text = result.getOutput();
        if (text != null) {
            processOutput(text);
        }
    }

    @Override
    public void sessionFinished() {
    }

    @Override
    public void outputLine(String line) {
        processLine(line);
    }

    private void processOutput(String text) {
        List<String> lines = toLines(text);
        lines.forEach(this::processLine);
    }

    private void processLine(String line) {
        if (line.trim().length() == 0) {
            return;
        }
        String lineLS = line + LINE_SEPARATOR;
        if (isLoggerLine(line)) {
            if (isLoggerError(line)) {
                appendText(lineLS, ContentType.ERROR);
            } else if (showLogs) {
                appendText(lineLS);
            }
            return;
        }

        if (lineLS.startsWith("(BeforeStories)") || lineLS.startsWith("(AfterStories)")) {
            appendText(lineLS, FontStyle.BOLD, JBColor.MAGENTA);
            return;
        }
        if (currentScenario == null) {
            appendText(lineLS);
            return;
        }
        if (lineLS.startsWith("Scenario:")) {
            String[] pair = splitFirstToken(lineLS);
            appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
            Location location = currentScenario.getLocation();
            appendHyperlink(pair[1], project -> {
                IdeUtil.openInEditor(project, location);
            });
            return;
        }
        if (lineLS.startsWith("Meta:") || lineLS.startsWith("@")) {
            String[] pair = splitFirstToken(lineLS);
            appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
            appendText(pair[1]);
            return;
        }
        if (lineLS.startsWith("Examples:")) {
            isExamples = true;
            return;
        }
        if (lineLS.startsWith("Example: ")) {
            isExamples = false;
            currentStep = null;
            appendText(lineLS, FontStyle.BOLD, JBColor.MAGENTA);
            return;
        }
        if (isExamples) {
            return;
        }
        if (isStepLine(line)) {
            AtomicReference<Status> statusHolder = new AtomicReference<>();
            line = removeStatus(line, statusHolder);
            String[] pair = splitFirstToken(line);
            appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
            appendText(pair[1]);

            currentStep = nextStep(line, !statusHolder.get().isFailed());
            if ((currentStep != null) && !currentStep.hasValues()) {
                appendStatusText(statusHolder.get());
            }
            appendText(LINE_SEPARATOR);
            return;
        }
        if (line.startsWith("|")) {
            AtomicReference<Status> statusHolder = new AtomicReference<>();
            line = removeStatus(line, statusHolder);
            boolean lastValue = false;
            if ((currentStep != null) && currentStep.hasValues()) {
                lastValue = currentStep.getValues().getLastRow().equals(Table.split(line));
            }
            if (showStepValues) {
                appendText("    " + line, null, JBColor.GRAY);
            } else if (lastValue) {
                appendText("    [...]", null, JBColor.GRAY);
            } else {
                return;
            }
            if (lastValue) {
                appendStatusText(statusHolder.get());
            }
            appendText(LINE_SEPARATOR);
            return;
        }
        appendText(lineLS);
    }

    private Step nextStep(String text, boolean matchText) {
        List<Step> steps = currentScenario.getSteps();
        int nextIndex = 0;
        if (currentStep != null) {
            nextIndex = steps.indexOf(currentStep) + 1;
        }
        if (nextIndex < steps.size()) {
            Step step = steps.get(nextIndex);
            if (matchText) {
                if (text.equals(step.getText())) {
                    return step;
                }
                String pattern = step.getPattern();
                if (!pattern.equals(step.getText())) {
                    MessageFormat format = new MessageFormat(pattern);
                    try {
                        format.parse(text);
                        return step;
                    } catch (ParseException e) { /* ignore */ }
                }
            } else {
                return step;
            }
        }
        return null;
    }

    private void appendStatusText(Status status) {
        appendText(' ' + status.text, FontStyle.BOLD, status.color);
    }

    private static List<String> toLines(String text) {
        return Arrays.stream(text.split("\n")).map(String::trim).collect(Collectors.toList());
    }

    private static boolean isLoggerLine(String line) {
        return isLoggerError(line) ||
                line.contains("WARN") ||
                line.contains("DEBUG") ||
                line.contains("INFO");
    }

    private static boolean isLoggerError(String line) {
        return line.contains("ERROR") && !line.contains("INFO");
    }

    private static boolean isStepLine(String line) {
        return line.startsWith("Given ") ||
               line.startsWith("And ") ||
               line.startsWith("When ") ||
               line.startsWith("Then ");
    }

    private static String removeStatus(String line, AtomicReference<Status> statusHolder) {
        for (Status status : Status.values()) {
            if (line.endsWith(status.text)) {
                statusHolder.set(status);
                return line.substring(0, line.length() - status.text.length()).trim();
            }
        }
        statusHolder.set(Status.PASSED);
        return line;
    }

    private static String[] splitFirstToken(String line) {
        int index = line.indexOf(' ');
        if (index > 0) {
            return new String[] { line.substring(0, index), line.substring(index)};
        }
        return new String[] { line, ""};
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
        boolean isFailed() { return this == FAILED; }
    }
}
