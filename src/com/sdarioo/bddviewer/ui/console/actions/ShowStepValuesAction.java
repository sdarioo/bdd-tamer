package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;

public class ShowStepValuesAction extends ToggleAction {

    private static final String TEXT = "View step values";
    private final LauncherConsole console;

    public ShowStepValuesAction(LauncherConsole console) {
        super(TEXT, TEXT, AllIcons.Nodes.DataTables);
        this.console = console;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return console.isShowStepValues();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean value) {
        console.setShowStepValues(value);
    }
}
