package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.console.OutputConsole;


public class ClearAction extends ActionBase {

    private static final String TEXT = "Clear";
    private final OutputConsole console;

    public ClearAction(OutputConsole console) {
        super(TEXT, AllIcons.Actions.GC);
        this.console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        console.clear();
    }
}
