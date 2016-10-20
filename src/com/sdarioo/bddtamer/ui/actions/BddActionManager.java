package com.sdarioo.bddtamer.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Separator;
import com.sdarioo.bddtamer.launcher.Launcher;
import com.sdarioo.bddtamer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddtamer.launcher.SessionManager;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.ui.tree.BddTree;

import java.util.Arrays;
import java.util.List;

public class BddActionManager {

    private final ActionBase runSelectedAction;
    private final ActionBase cleanResultsAction;
    private final ActionBase expandAllAction;
    private final ActionBase collapseAllAction;
    private final ActionBase reloadTreeAction;


    public BddActionManager(BddTree tree, SessionManager sessionManager) {

        cleanResultsAction = new ClearResultsAction(tree, sessionManager);
        runSelectedAction = new RunSelectedAction(tree, sessionManager.getLauncher());

        expandAllAction = new ExpandAction(tree.getTreeTable());
        collapseAllAction = new CollapseAction(tree.getTreeTable());
        reloadTreeAction = new ReloadAction(tree);

        addLauncherListener(sessionManager.getLauncher());

    }

    public List<AnAction> getToolbarAction() {
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
