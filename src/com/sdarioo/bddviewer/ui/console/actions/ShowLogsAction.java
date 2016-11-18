package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.sdarioo.bddviewer.ui.console.OutputConsole;

public class ShowLogsAction extends ToggleAction {

    private static final String TEXT = "View logs";
    private final OutputConsole console;

    public ShowLogsAction(OutputConsole console) {
        super(TEXT, TEXT, AllIcons.General.Warning);
        this.console = console;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return console.isShowLogs();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean value) {
        console.setShowLogs(value);
    }
}
