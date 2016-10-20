package com.sdarioo.bddtamer.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddtamer.launcher.SessionManager;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.ui.tree.BddTree;
import com.sdarioo.bddtamer.ui.util.TreeUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ClearResultsAction extends ActionBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearResultsAction.class);
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
        sessionManager.clear();
        scenariosToRefresh.forEach(s -> {
            DefaultTreeTableNode node = TreeUtil.findNode(tree.getRootNode(), s);
            if (node != null) {
                tree.refreshNode(node);
            }
        });
    }
}