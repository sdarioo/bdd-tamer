package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddtamer.StoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class BddTreeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeView.class);

    private final Project project;
    private final StoryProvider storyProvider;

    private JPanel rootComponent;
    private BddActionManager actionManager;

    public BddTreeView(Project project, StoryProvider storyProvider) {
        this.project = project;
        this.storyProvider = storyProvider;

        initializeUI();
    }

    public JComponent getComponent() {
        return rootComponent;
    }

    public BddActionManager getActionManager() {
        return actionManager;
    }

    private void initializeUI() {

        rootComponent = new JPanel(new BorderLayout());

        BddTree tree = new BddTree(project, storyProvider);
        JBScrollPane treePane = new JBScrollPane(tree.getTreeTable());
        SearchComponent searchComponent = new SearchComponent(tree, rootComponent, project);

        rootComponent.add(searchComponent.getComponent(), BorderLayout.NORTH);
        rootComponent.add(treePane, BorderLayout.CENTER);

        actionManager = new BddActionManager(tree);
    }

}
