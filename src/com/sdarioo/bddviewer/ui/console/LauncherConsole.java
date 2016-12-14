package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.model.Scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LauncherConsole extends AbstractConsole {
    private boolean isShowDetails = false;
    private final ConsoleNavigator navigator;

    public LauncherConsole(Project project) {
        super(project);
        navigator = new ConsoleNavigator();
        Launcher launcher = Plugin.getInstance().getLauncher(project);
        launcher.addListener(navigator);
    }

    @Override
    public void clear() {
        super.clear();
        navigator.clear();
    }

    public boolean isShowDetails() {
        return isShowDetails;
    }

    public void setShowDetails(boolean value) {
        isShowDetails = value;
    }

    public void scrollTo(Scenario scenario) {
        LogicalPosition startPosition = editor.offsetToLogicalPosition(0);
        LogicalPosition endPosition = startPosition;

        Integer offset = navigator.getOffset(scenario);
        if ((offset != null) && (offset < getTextLength())) {
            startPosition = editor.offsetToLogicalPosition(offset);
            endPosition = new LogicalPosition(startPosition.line, Integer.MAX_VALUE);
        } else {
            LOGGER.warn("Missing or invalid offset for scenario:  " + scenario.getName());
        }
        editor.getScrollingModel().scrollTo(startPosition, ScrollType.CENTER_UP);
        editor.getSelectionModel().setBlockSelection(startPosition, endPosition);
    }

    private class ConsoleNavigator extends LauncherListenerAdapter{
        private final Map<Scenario, Integer> scenarioLocations = new ConcurrentHashMap<>();

        @Override
        public void scenarioStarted(Scenario scenario) {
            UIUtil.invokeLaterIfNeeded(() -> {
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
