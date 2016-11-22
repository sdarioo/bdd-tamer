package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.sdarioo.bddviewer.launcher.*;
import com.sdarioo.bddviewer.model.Location;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Step;
import com.sdarioo.bddviewer.ui.util.IdeUtil;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class LauncherConsole extends AbstractConsole implements LauncherListener {

    private boolean showLogs;

    private Scenario runningScenario;
    private Step runningStep;


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

    @Override
    public void sessionStarted(List<Scenario> scope) {
        clear();
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        runningScenario = scenario;
        runningStep = null;
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
        runningScenario = null;
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
        if (isLoggerLine(line)) {
            if (isLoggerError(line)) {
                appendText(line + LINE_SEPARATOR, ContentType.ERROR);
            } else if (showLogs) {
                appendText(line + LINE_SEPARATOR);
            }
            return;
        }

        if (runningScenario != null) {
            if (line.startsWith("Scenario:")) {
                String[] pair = splitFirstToken(line);
                appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
                Location location = runningScenario.getLocation();
                appendHyperlink(pair[1], project -> {
                    IdeUtil.openInEditor(project, location);
                });
            } else if (line.startsWith("Meta:") || line.startsWith("@")) {
                String[] pair = splitFirstToken(line);
                appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
                appendText(pair[1]);
            } else if (isStep(line)) {
                AtomicReference<Status> statusHolder = new AtomicReference<>();
                line = removeStatus(line, statusHolder);
                String[] pair = splitFirstToken(line);
                appendText(pair[0], FontStyle.BOLD, JBColor.ORANGE);
                appendText(pair[1]);

                runningStep = getStep(line);
                if ((runningStep != null) && !runningStep.hasValues()) {
                    appendText(' ' + statusHolder.get().text, FontStyle.BOLD, statusHolder.get().color);
                }

            } else if (line.startsWith("|")) {


                appendText("    " + line, null, JBColor.GRAY);
            } else if (line.startsWith("Example: ")) {
                runningStep = null;
                appendText(line, null, JBColor.YELLOW);
            } else {
                appendText(line);
            }
        } else {
            appendText(line);
        }
        appendText(LINE_SEPARATOR);
    }

    private Step getStep(String text) {
        List<Step> steps = runningScenario.getSteps();
        int index = 0;
        if (runningStep != null) {
            index = steps.indexOf(runningStep) + 1;
        }
        if (index < steps.size()) {
            Step step = steps.get(index);
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
        }
        return null;
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

    private static boolean isStep(String line) {
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
    }
}
