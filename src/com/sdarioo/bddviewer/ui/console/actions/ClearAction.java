package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;


public class ClearAction extends ActionBase {

    private static final String TEXT = "Clear";
    private final LauncherConsole console;

    public ClearAction(LauncherConsole console) {
        super(TEXT, AllIcons.Actions.GC);
        this.console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        console.clear();
    }
}
