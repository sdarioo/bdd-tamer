package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;

public class ShowDetailsAction extends ToggleAction {

    public static final String TEXT = "Show Details";
    private final LauncherConsole console;

    public ShowDetailsAction(LauncherConsole console) {
        super(TEXT, TEXT, AllIcons.Actions.PreviewDetails);
        this.console = console;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return console.isShowDetails();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean value) {
        console.setShowDetails(value);
    }
}
