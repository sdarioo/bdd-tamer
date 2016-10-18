package com.sdarioo.bddtamer.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddtamer.ui.BddTree;

public class RefreshBddAction extends AnAction {

    private static final String TEXT = "Refresh Tree";

    private final BddTree tree;

    public RefreshBddAction(BddTree tree) {
        super(TEXT, TEXT, AllIcons.Actions.Refresh);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        tree.refresh();
    }
}
