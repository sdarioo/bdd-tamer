package com.sdarioo.bddtamer.ui.actions;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sdarioo.bddtamer.launcher.Launcher;
import com.sdarioo.bddtamer.launcher.LauncherException;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import com.sdarioo.bddtamer.ui.BddTree;
import com.sdarioo.bddtamer.ui.util.TreeUtil;
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
    private ActionBase cleanResultsAction;

    public RunSelectedAction(BddTree tree, Launcher launcher, ActionBase cleanResultsAction) {
        super(TEXT, AllIcons.Toolwindows.ToolWindowRun);
        this.tree = tree;
        this.launcher = launcher;
        this.cleanResultsAction = cleanResultsAction;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        TreePath[] paths = tree.getTreeTable().getSelectionPaths();
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
            cleanResultsAction.actionPerformed(anActionEvent);
            launcher.submit(new ArrayList<>(scope));
        } catch (LauncherException e) {
            LOGGER.error(e.toString());
        }

    }
}
