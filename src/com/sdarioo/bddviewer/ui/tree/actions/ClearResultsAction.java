package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Story;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ClearResultsAction extends ActionBase {

    private static final Logger LOGGER = Logger.getInstance(ClearResultsAction.class);
    private static final String TEXT = "Clear Test Results";

    private final BddTree tree;
    private final SessionManager sessionManager;

    public ClearResultsAction(BddTree tree, SessionManager sessionManager) {
        super(TEXT, AllIcons.General.Reset);

        this.tree = tree;
        this.sessionManager = sessionManager;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (sessionManager.isRunning()) {
            LOGGER.warn("Cannot clear during running session.");
            return;
        }
        List<Scenario> scenariosToRefresh = new ArrayList<>(sessionManager.getFinishedScenarios());
        Set<Story> storiesToRefresh = scenariosToRefresh.stream().map(Scenario::getStory).collect(Collectors.toSet());

        sessionManager.clear();

        refreshNodes(scenariosToRefresh);
        refreshNodes(storiesToRefresh);
    }

    private void refreshNodes(Collection<? extends Object> userObjects) {
        userObjects.forEach(obj -> {
            DefaultTreeTableNode node = TreeUtil.findNode(tree.getModel(), obj);
            if (node != null) {
                tree.refreshNode(node);
            }
        });
    }
}
