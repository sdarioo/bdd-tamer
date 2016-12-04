package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;

public class VerboseOutputAction extends ToggleAction {

    private static final String TEXT = "Verbose output";
    private final LauncherConsole console;

    public VerboseOutputAction(LauncherConsole console) {
        super(TEXT, TEXT, AllIcons.Actions.PreviewDetails);
        this.console = console;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return console.isVerboseMode();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean value) {
        console.setVerboseMode(value);
    }
}
