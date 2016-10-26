package com.sdarioo.bddtamer.ui.tree;

import com.intellij.icons.AllIcons;
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
import java.nio.file.Path;

public class BddIconMap implements IconMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddIconMap.class);

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
            return (icon != null) ? icon : AllIcons.FileTypes.Text;
        }
        return null;
    }
}
