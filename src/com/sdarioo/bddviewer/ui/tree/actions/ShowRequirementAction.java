package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.util.TreeUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ShowRequirementAction  extends ActionBase {

    private static final String TEXT = "Show Requirement";

    private final BddTree tree;

    public ShowRequirementAction(BddTree tree) {
        super(TEXT, AllIcons.Actions.Help);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        TreePath[] paths = tree.getTreeTable().getSelectionPaths();
        if ((paths == null) || (paths.length == 0)) {
            return;
        }
        List<String> req = new ArrayList<>();
        for (TreePath path : paths) {
            Object userObject = TreeUtil.getUserObject(path);
            if (userObject instanceof Scenario) {
                req.addAll(((Scenario) userObject).getMeta().getRequirementsList());
            }
        }
        CustomDialog dialog = new CustomDialog("Requirements");
       // dialog.showDialog(Intellinwi);

    }


    public class CustomDialog extends JDialog {
        public CustomDialog(String title) {
            setModal(true);
            setResizable(false);
            setTitle(title);

            buildGUI();
        }

        public void showDialog(Window parent) {
            setLocationRelativeTo(parent);
            setVisible(true);
        }

        private void buildGUI() {

        }
    }
}
