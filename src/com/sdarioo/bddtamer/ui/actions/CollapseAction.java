package com.sdarioo.bddtamer.ui.actions;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.sciss.treetable.j.TreeTable;

public class CollapseAction extends AnAction {

    private static final String TEXT = "Collapse All";

    private final TreeTable tree;

    public CollapseAction(TreeTable tree) {
        super(TEXT, TEXT, AllIcons.Actions.Collapseall);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.collapseRow(i);
        }
    }
}
