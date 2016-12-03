package com.sdarioo.bddviewer.ui.console;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.Plugin;


public class LauncherConsole extends AbstractConsole {

    private final LauncherOutputFormatter formatter;

    public LauncherConsole(Project project) {
        super(project);
        formatter = new LauncherOutputFormatter(this);
        Plugin.getInstance().getLauncher(project).addListener(formatter);
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

}
