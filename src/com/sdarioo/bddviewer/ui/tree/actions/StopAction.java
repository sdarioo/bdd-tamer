package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopAction extends ActionBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopAction.class);
    private static final String TEXT = "Kill Process";

    private final Launcher launcher;

    public StopAction(Launcher launcher) {
        super(TEXT, AllIcons.Debugger.KillProcess);
        this.launcher = launcher;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        launcher.terminate();
    }
}
