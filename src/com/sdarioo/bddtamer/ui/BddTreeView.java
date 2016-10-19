package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddtamer.StoryProvider;
import com.sdarioo.bddtamer.ui.search.SearchComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class BddTreeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeView.class);

    private final Project project;
    private final StoryProvider storyProvider;

    private JPanel rootComponent;
    private BddTree tree;
    private SearchComponent searchComponent;

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

        tree = new BddTree(project, storyProvider);
        searchComponent = new SearchComponent(tree.getTreeTable());

        rootComponent.add(searchComponent.getComponent(), BorderLayout.NORTH);
        rootComponent.add(new JBScrollPane(tree.getTreeTable()), BorderLayout.CENTER);

        actionManager = new BddActionManager(tree);
    }

}
