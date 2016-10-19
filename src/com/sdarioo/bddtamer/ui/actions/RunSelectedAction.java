package com.sdarioo.bddtamer.ui.actions;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class RunSelectedAction extends AnAction {

    private static final String TEXT = "Run Selected Tests";

    public RunSelectedAction() {
        super(TEXT, TEXT, AllIcons.Toolwindows.ToolWindowRun);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

    }
}
