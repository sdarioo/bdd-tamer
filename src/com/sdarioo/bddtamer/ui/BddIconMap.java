package com.sdarioo.bddtamer.ui;

import com.intellij.icons.AllIcons;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import de.sciss.treetable.j.IconMap;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class BddIconMap implements IconMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddIconMap.class);

    @Override
    public Icon getIcon(TreeTable treeTable, Object node, boolean expanded, boolean leaf) {
        if (!(node instanceof BddTreeNode)) {
            LOGGER.warn("Expected BddTreeNode. Got: " + node.getClass().getName());
            return AllIcons.General.TodoQuestion;
        }
        Object modelObject = ((BddTreeNode)node).getModelObject();
        if (modelObject instanceof Story) {
            return AllIcons.Nodes.TestSourceFolder;
        }
        if (modelObject instanceof Scenario) {
            return AllIcons.Nodes.JunitTestMark;
        }
        return AllIcons.General.TodoQuestion;
    }
}
