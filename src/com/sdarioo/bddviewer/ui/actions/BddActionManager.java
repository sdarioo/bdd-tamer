package com.sdarioo.bddviewer.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Separator;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.BddTreeColumns;

import java.util.Arrays;
import java.util.List;

public class BddActionManager {

    private final ActionBase runSelectedAction;
    private final ActionBase cleanResultsAction;
    private final ActionBase expandAllAction;
    private final ActionBase collapseAllAction;
    private final ActionBase reloadTreeAction;
    private final ActionBase copyL2Action;


    public BddActionManager(BddTree tree, SessionManager sessionManager) {

        cleanResultsAction = new ClearResultsAction(tree, sessionManager);
        runSelectedAction = new RunSelectedAction(tree, sessionManager.getLauncher());

        expandAllAction = new ExpandAction(tree.getTreeTable());
        collapseAllAction = new CollapseAction(tree.getTreeTable());
        reloadTreeAction = new ReloadAction(tree);
        copyL2Action = new CopyAction(tree.getTreeTable(), BddTreeColumns.L2_COLUMN);

        addLauncherListener(sessionManager.getLauncher());

    }

    public List<AnAction> getContextMenuActions() {
        return Arrays.asList(runSelectedAction, copyL2Action);
    }

    public List<AnAction> getToolbarActions() {
        return Arrays.asList(runSelectedAction,
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
            }
            @Override
            public void sessionFinished() {
                setEnabled(runSelectedAction, true);
                setEnabled(cleanResultsAction, true);
                setEnabled(reloadTreeAction, true);
            }
        });
    }

    private void setEnabled(ActionBase action, boolean value) {
        action.setEnabled(value);
    }
}
