package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.model.Scenario;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LauncherConsole extends AbstractConsole {

    private final LauncherOutputFormatter formatter;
    private final ConsoleNavigator navigator;

    public LauncherConsole(Project project) {
        super(project);
        formatter = new LauncherOutputFormatter(this);
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Compact);
        navigator = new ConsoleNavigator();

        Launcher launcher = Plugin.getInstance().getLauncher(project);
        launcher.addListener(formatter);
        launcher.addListener(navigator);
    }

    @Override
    public void clear() {
        super.clear();
        navigator.clear();
    }

    public boolean isShowLogs() {
        return formatter.getFormatterMode() == LauncherOutputFormatter.FormatterMode.Full;
    }

    public void setShowLogs(boolean value) {
        if (value) {
            formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Full);
        } else {
            formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Full);
        }
    }

    public boolean isShowStepValues() {
        return formatter.getFormatterMode().value >= LauncherOutputFormatter.FormatterMode.Extended.value;
    }

    public void setShowStepValues(boolean showStepValues) {
        formatter.setFormatterMode(LauncherOutputFormatter.FormatterMode.Extended);
    }

    public void scrollTo(Scenario scenario) {
        Integer offset = navigator.getOffset(scenario);
        if (offset == null) {
            LOGGER.warn("There is no recorded console offset for scenario:  " + scenario.getName());
            return;
        }
        if (offset >= getTextLength()) {
            LOGGER.warn("Invalid scenario offset:  " + offset.intValue() + ". Text length: " + getTextLength());
            return;
        }
        LogicalPosition startPosition = editor.offsetToLogicalPosition(offset);
        LogicalPosition endPosition = new LogicalPosition(startPosition.line, Integer.MAX_VALUE);

        editor.getScrollingModel().scrollTo(startPosition, ScrollType.CENTER_UP);
        editor.getSelectionModel().setBlockSelection(startPosition, endPosition);
    }

    private class ConsoleNavigator extends LauncherListenerAdapter{
        private final Map<Scenario, Integer> scenarioLocations = new ConcurrentHashMap<>();

        @Override
        public void scenarioStarted(Scenario scenario) {
            SwingUtilities.invokeLater(() -> {
                scenarioLocations.put(scenario, getTextLength());
            });
        }

        void clear() {
            scenarioLocations.clear();
        }

        Integer getOffset(Scenario scenario) {
            return scenarioLocations.get(scenario);
        }
    }

}
