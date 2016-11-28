package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.search.SearchHelper;
import de.sciss.treetable.j.DefaultTreeTableNode;

import java.util.function.Predicate;

public class NextResultAction extends ActionBase {

    private static final String TEXT = "Next Result";

    private final BddTree tree;
    private final SessionManager sessionManager;
    private final boolean forward;

    public NextResultAction(BddTree tree, SessionManager sessionManager, boolean forward) {
        super(TEXT, forward ? AllIcons.Actions.NextOccurence : AllIcons.Actions.PreviousOccurence);
        this.tree = tree;
        this.sessionManager = sessionManager;
        this.forward = forward;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Predicate<DefaultTreeTableNode> matcher = node -> {
            Object userObject = node.getUserObject();
            if (userObject instanceof Scenario) {
                return (sessionManager.getResult((Scenario)userObject) != null);
            }
            return false;
        };
        SearchHelper helper = new SearchHelper(matcher);
        if (forward) {
            helper.findNext(tree.getTreeTable());
        } else {
            helper.findPrev(tree.getTreeTable());
        }
    }
}
