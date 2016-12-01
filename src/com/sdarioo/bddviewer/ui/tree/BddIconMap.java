package com.sdarioo.bddviewer.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.sdarioo.bddviewer.launcher.RunStatus;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Story;
import de.sciss.treetable.j.IconMap;
import de.sciss.treetable.j.TreeTable;

import javax.swing.*;
import java.nio.file.Path;

public class BddIconMap implements IconMap {

    private static final Logger LOGGER = Logger.getInstance(BddIconMap.class);

    private final SessionManager sessionManager;

    public BddIconMap(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Icon getIcon(TreeTable treeTable, Object userObject, boolean expanded, boolean leaf) {

        if (userObject instanceof Path) {
            return AllIcons.Nodes.Folder;
        }
        if (userObject instanceof Story) {
            return AllIcons.Nodes.Class;
        }
        if (userObject instanceof Scenario) {
            Scenario scenario = (Scenario)userObject;

            Icon icon = null;
            if (sessionManager.isPending(scenario)) {
                icon = AllIcons.RunConfigurations.TestNotRan;
            } else if (sessionManager.isRunning(scenario)) {
                icon = AllIcons.Diff.CurrentLine;
            } else {
                TestResult result = sessionManager.getResult(scenario);
                if (result != null) {
                    if (RunStatus.Passed.equals(result.getStatus())) {
                        icon = AllIcons.RunConfigurations.TestPassed;
                    } else if (RunStatus.Failed.equals(result.getStatus())) {
                        icon = AllIcons.RunConfigurations.TestFailed;
                    } else {
                        icon = AllIcons.RunConfigurations.TestIgnored;
                    }
                }
            }
            return (icon != null) ? icon : AllIcons.General.Bullet;
            //return (icon != null) ? icon : AllIcons.Ide.HectorOn;
        }
        return null;
    }
}
