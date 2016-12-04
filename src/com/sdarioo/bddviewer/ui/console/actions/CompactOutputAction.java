package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;

public class CompactOutputAction extends ToggleAction {

    private static final String TEXT = "Compacted output";
    private final LauncherConsole console;

    public CompactOutputAction(LauncherConsole console) {
        super(TEXT, TEXT, AllIcons.ObjectBrowser.CompactEmptyPackages);
        this.console = console;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return console.isCompactMode();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean value) {
        console.setCompactMode(value);
    }
}
