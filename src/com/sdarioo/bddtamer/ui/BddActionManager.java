package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Separator;
import com.sdarioo.bddtamer.ui.actions.CollapseAction;
import com.sdarioo.bddtamer.ui.actions.ExpandAction;
import com.sdarioo.bddtamer.ui.actions.RefreshBddAction;
import com.sdarioo.bddtamer.ui.actions.RunSelectedAction;

import java.util.Arrays;
import java.util.List;

public class BddActionManager {

    private final BddTree tree;

    BddActionManager(BddTree tree) {
        this.tree = tree;
    }

    public List<AnAction> getToolbarAction() {
        return Arrays.asList(
                new RunSelectedAction(),
                Separator.getInstance(),
                new ExpandAction(tree.getTreeTable()),
                new CollapseAction(tree.getTreeTable()),
                Separator.getInstance(),
                new RefreshBddAction(tree));
    }
}
