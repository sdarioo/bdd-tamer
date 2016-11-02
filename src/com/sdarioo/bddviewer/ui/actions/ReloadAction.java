package com.sdarioo.bddviewer.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.ui.tree.BddTree;

public class ReloadAction extends ActionBase {

    private static final String TEXT = "Reload Tree";

    private final BddTree tree;

    public ReloadAction(BddTree tree) {
        super(TEXT, AllIcons.Actions.Refresh);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        tree.getActionManager().getCleanResultsAction().actionPerformed(anActionEvent);
        tree.reloadTree();
    }
}
