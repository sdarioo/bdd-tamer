package com.sdarioo.bddviewer.ui.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.BddToolWindowFactory;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.util.TreeUtil;

import javax.swing.tree.TreePath;

public class ShowOutputAction extends ActionBase {

    private static final Logger LOGGER = Logger.getInstance(ShowOutputAction.class);
    private static final String TEXT = "Show Output";

    private final Project project;
    private final BddTree tree;
    private final LauncherConsole console;

    public ShowOutputAction(Project project, BddTree tree, LauncherConsole console) {
        super(TEXT, AllIcons.Debugger.Console);
        this.project = project;
        this.tree = tree;
        this.console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        BddToolWindowFactory.selectContent(project, BddToolWindowFactory.CONSOLE_CONTENT_ID);
        Scenario scenario = getSelectedScenario();
        if (scenario != null) {
            console.scrollTo(scenario);
        }
    }

    private Scenario getSelectedScenario() {
        TreePath[] paths = tree.getTreeTable().getSelectionPaths();
        if ((paths == null) || (paths.length == 0)) {
            return null;
        }
        for (TreePath path : paths) {
            Object userObject = TreeUtil.getUserObject(path);
            if (userObject instanceof Scenario) {
                return (Scenario)userObject;
            }
        }
        return null;
    }
}
