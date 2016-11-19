package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;

import java.util.List;


public class OutputConsole extends AbstractConsole {

    private boolean showLogs = false;

    public OutputConsole(Project project, SessionManager sessionManager) {
        super(project);
        addLaunchListener(sessionManager.getLauncher());
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean value) {
        showLogs = value;
    }

    private void addLaunchListener(Launcher launcher) {
        launcher.addListener(new LauncherListenerAdapter() {
            @Override
            public void sessionStarted(List<Scenario> scope) {
                clear();
            }
            @Override
            public void scenarioStarted(Scenario scenario) {
                appendText(scenario.getName(), FontStyle.BOLD);
                appendHyperlink(" [GoTo]", project -> {});
                appendText(LINE_SEPARATOR);
            }
            @Override
            public void scenarioFinished(Scenario scenario, TestResult result) {
                String text = result.getOutput();
                if (text != null) {
                    appendText(text, ContentType.ERROR);
                    appendText(LINE_SEPARATOR);
                }
            }
            @Override
            public void output(String message) {
                appendText(message, ContentType.ERROR);
            }
        });
    }

    private static boolean isLoggerLine(String line) {
        return line.contains("WARN") || line.contains("ERROR") || line.contains("DEBUG") || line.contains("INFO");
    }

}
