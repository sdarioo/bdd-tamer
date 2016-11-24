package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.BddTreeColumns;

import java.util.Arrays;
import java.util.List;

public class BddTreeActionManager {

    private final ActionBase runSelectedAction;
    private final ActionBase stopAction;
    private final ActionBase cleanResultsAction;
    private final ActionBase expandAllAction;
    private final ActionBase collapseAllAction;
    private final ActionBase reloadTreeAction;
    private final ActionBase copyL2Action;


    public BddTreeActionManager(BddTree tree, Project project) {

        SessionManager sessionManager = Plugin.getInstance().getSessionManager(project);
        Launcher launcher = Plugin.getInstance().getLauncher(project);

        cleanResultsAction = new ClearResultsAction(tree, sessionManager);
        runSelectedAction = new RunSelectedAction(tree, launcher);
        stopAction = new StopAction(launcher);
        stopAction.setEnabled(false);

        expandAllAction = new ExpandAction(tree.getTreeTable());
        collapseAllAction = new CollapseAction(tree.getTreeTable());
        reloadTreeAction = new ReloadAction(tree);
        copyL2Action = new CopyAction(tree.getTreeTable(), BddTreeColumns.L2_COLUMN);

        addLauncherListener(launcher);

    }

    public List<AnAction> getContextMenuActions() {
        return Arrays.asList(runSelectedAction, copyL2Action);
    }

    public List<AnAction> getToolbarActions() {
        return Arrays.asList(runSelectedAction,
                stopAction,
                cleanResultsAction,
                Separator.getInstance(),
                expandAllAction,
                collapseAllAction,
                Separator.getInstance(),
                reloadTreeAction);
    }

    public AnAction getRunSelectedAction() {
        return runSelectedAction;
    }

    public AnAction getCleanResultsAction() {
        return cleanResultsAction;
    }

    public AnAction getExpandAllAction() {
        return expandAllAction;
    }

    public AnAction getCollapseAllAction() {
        return collapseAllAction;
    }

    public AnAction getReloadTreeAction() {
        return reloadTreeAction;
    }

    private void addLauncherListener(Launcher launcher) {
        launcher.addListener(new LauncherListenerAdapter() {
            @Override
            public void sessionStarted(List<Scenario> scope) {
                setEnabled(runSelectedAction, false);
                setEnabled(cleanResultsAction, false);
                setEnabled(reloadTreeAction, false);
                setEnabled(stopAction, true);
            }
            @Override
            public void sessionFinished() {
                setEnabled(runSelectedAction, true);
                setEnabled(cleanResultsAction, true);
                setEnabled(reloadTreeAction, true);
                setEnabled(stopAction, false);
            }
        });
    }

    private void setEnabled(ActionBase action, boolean value) {
        action.setEnabled(value);
    }
}
