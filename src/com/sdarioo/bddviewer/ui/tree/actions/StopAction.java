package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.ui.actions.ActionBase;


public class StopAction extends ActionBase {

    private static final Logger LOGGER = Logger.getInstance(StopAction.class);
    private static final String TEXT = "Kill Process";

    private final Launcher launcher;

    public StopAction(Launcher launcher) {
        super(TEXT, AllIcons.Actions.Suspend);
        this.launcher = launcher;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        launcher.terminate();
    }
}
