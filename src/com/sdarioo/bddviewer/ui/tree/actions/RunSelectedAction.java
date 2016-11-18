package com.sdarioo.bddviewer.ui.tree.actions;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherException;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Story;
import com.sdarioo.bddviewer.ui.actions.ActionBase;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.util.TreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class RunSelectedAction extends ActionBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunSelectedAction.class);

    private static final String TEXT = "Run Selected Tests";

    private final BddTree tree;
    private final Launcher launcher;

    public RunSelectedAction(BddTree tree, Launcher launcher) {
        super(TEXT, AllIcons.Toolwindows.ToolWindowRun);
        this.tree = tree;
        this.launcher = launcher;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        TreePath[] paths = tree.getTreeTable().getSelectionPaths();
        if ((paths == null) || (paths.length == 0)) {
            return;
        }
        Set<Scenario> scope = new LinkedHashSet<>();

        for (TreePath path : paths) {
            Object userObject = TreeUtil.getUserObject(path);
            if (userObject instanceof Story) {
                scope.addAll(((Story) userObject).getScenarios());
            } else if (userObject instanceof Scenario) {
                scope.add((Scenario)userObject);
            }
        }
        try {
            tree.getActionManager().getCleanResultsAction().actionPerformed(anActionEvent);
            launcher.submit(new ArrayList<>(scope));
        } catch (LauncherException e) {
            LOGGER.error(e.toString());
        }

    }
}
