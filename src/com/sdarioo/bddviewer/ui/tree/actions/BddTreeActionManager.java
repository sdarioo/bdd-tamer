package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionContext;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.BddTreeColumns;

import java.util.Arrays;
import java.util.List;

public class BddTreeActionManager {

    private final ActionBase runSelectedAction;
    private final ActionBase stopAction;
    private final ActionBase cleanResultsAction;
    private final ActionBase nextResultAction;
    private final ActionBase prevResultAction;
    private final ActionBase expandAllAction;
    private final ActionBase collapseAllAction;
    private final ActionBase reloadTreeAction;
    private final ActionBase copyL2Action;
    private final ActionBase showConsoleOutputAction;


    public BddTreeActionManager(Project project, BddTree tree, LauncherConsole console) {

        SessionManager sessionManager = Plugin.getInstance().getSessionManager(project);
        Launcher launcher = Plugin.getInstance().getLauncher(project);

        runSelectedAction = new RunSelectedAction(tree, launcher);
        stopAction = new StopAction(launcher);
        stopAction.setEnabled(false);

        cleanResultsAction = new ClearResultsAction(tree, sessionManager);
        nextResultAction = new NextResultAction(tree, sessionManager, true);
        prevResultAction = new NextResultAction(tree, sessionManager, false);

        expandAllAction = new ExpandAction(tree.getTreeTable());
        collapseAllAction = new CollapseAction(tree.getTreeTable());
        reloadTreeAction = new ReloadAction(tree);
        copyL2Action = new CopyAction(tree.getTreeTable(), BddTreeColumns.L2_COLUMN);
        showConsoleOutputAction = new ShowOutputAction(project, tree, console);

        addLauncherListener(launcher);

    }

    public List<AnAction> getContextMenuActions() {
        return Arrays.asList(runSelectedAction,
                copyL2Action,
                showConsoleOutputAction);
    }

    public List<AnAction> getToolbarActions() {
        return Arrays.asList(runSelectedAction,
                stopAction,
                Separator.getInstance(),
                cleanResultsAction,
                nextResultAction,
                prevResultAction,
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
            public void sessionStarted(List<Scenario> scope, SessionContext context) {
                setEnabled(runSelectedAction, false);
                setEnabled(cleanResultsAction, false);
                setEnabled(reloadTreeAction, false);
                setEnabled(stopAction, true);
            }
            @Override
            public void sessionFinished(SessionContext context) {
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
