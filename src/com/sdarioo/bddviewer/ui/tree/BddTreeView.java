package com.sdarioo.bddviewer.ui.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.provider.StoryProvider;
import com.sdarioo.bddviewer.ui.actions.BddActionManager;
import com.sdarioo.bddviewer.ui.search.SearchComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class BddTreeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeView.class);

    private BddTree tree;
    private BddActionManager actionManager;

    private JPanel rootComponent;
    private SearchComponent searchComponent;

    public BddTreeView(Project project,
                       StoryProvider storyProvider,
                       SessionManager sessionManager) {
        initializeUI(project, storyProvider, sessionManager);
    }

    public JComponent getComponent() {
        return rootComponent;
    }

    public BddActionManager getActionManager() {
        return actionManager;
    }

    private void initializeUI(Project project,
                              StoryProvider storyProvider,
                              SessionManager sessionManager) {

        rootComponent = new JPanel(new BorderLayout());

        tree = new BddTree(project, storyProvider, sessionManager);
        searchComponent = new SearchComponent(rootComponent, tree.getTreeTable());

        rootComponent.add(searchComponent.getComponent(), BorderLayout.NORTH);
        rootComponent.add(new JBScrollPane(tree.getTreeTable()), BorderLayout.CENTER);

        actionManager = new BddActionManager(tree, sessionManager);
        tree.setActionManager(actionManager);
    }

}
