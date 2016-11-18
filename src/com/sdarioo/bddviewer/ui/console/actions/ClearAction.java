package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.console.OutputConsole;


public class ClearAction extends ActionBase {

    private static final String TEXT = "Clear";

    public ClearAction(OutputConsole console) {
        super(TEXT, AllIcons.Actions.Clean);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

    }
}
