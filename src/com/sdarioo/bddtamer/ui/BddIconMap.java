package com.sdarioo.bddtamer.ui;

import com.intellij.icons.AllIcons;
import com.sdarioo.bddtamer.Plugin;
import com.sdarioo.bddtamer.launcher.RunStatus;
import com.sdarioo.bddtamer.launcher.SessionManager;
import com.sdarioo.bddtamer.launcher.TestResult;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import de.sciss.treetable.j.IconMap;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class BddIconMap implements IconMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddIconMap.class);

    private final SessionManager sessionManager;

    public BddIconMap(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Icon getIcon(TreeTable treeTable, Object node, boolean expanded, boolean leaf) {

        if (node instanceof Story) {
            return AllIcons.Nodes.Folder;
        }
        if (node instanceof Scenario) {
            Scenario scenario = (Scenario)node;

            Icon icon = null;
            if (sessionManager.isPending(scenario)) {
                icon = AllIcons.RunConfigurations.TestNotRan;
            } else if (sessionManager.isRunning(scenario)) {
                icon = AllIcons.RunConfigurations.TestInProgress1;
            } else {
                TestResult result = sessionManager.getResult(scenario);
                if (result != null) {
                    if (RunStatus.Passed.equals(result.getStatus())) {
                        icon = AllIcons.RunConfigurations.TestPassed;
                    } else if (RunStatus.Failed.equals(result.getStatus())) {
                        icon = AllIcons.RunConfigurations.TestFailed;
                    } else {
                        icon = AllIcons.RunConfigurations.TestSkipped;
                    }
                }
            }
            return (icon != null) ? icon : AllIcons.General.Bullet;
        }
        return null;
    }
}
