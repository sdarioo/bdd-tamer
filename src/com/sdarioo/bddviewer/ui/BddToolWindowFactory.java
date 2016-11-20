package com.sdarioo.bddviewer.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.ui.tree.actions.BddTreeActionManager;
import com.sdarioo.bddviewer.ui.console.actions.ConsoleActionManager;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;
import com.sdarioo.bddviewer.ui.tree.search.SearchComponent;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;


//http://www.programcreek.com/java-api-examples/index.php?source_dir=platform_tools_adt_idea-master/android/src/com/android/tools/idea/editors/vmtrace/TraceViewPanel.java
public class BddToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddToolWindowFactory.class);
    private static final String TREE_LABEL = "Tree";
    private static final String CONSOLE_LABEL = "Output";

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        Content treeContent = createTreeContent(project);
        Content consoleContent = createConsoleContent(project);

        toolWindow.getContentManager().addContent(treeContent);
        toolWindow.getContentManager().addContent(consoleContent);

        toolWindow.setIcon(AllIcons.Toolwindows.Documentation);
    }

    private static Content createTreeContent(Project project) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, TREE_LABEL, false);

        BddTree tree = new BddTree(project,
                Plugin.getInstance().getStoryProvider(),
                Plugin.getInstance().getSessionManager());

        JPanel treePanel = new JPanel(new BorderLayout());
        SearchComponent searchPanel = new SearchComponent(treePanel, tree.getTreeTable());
        treePanel.add(searchPanel.getComponent(), BorderLayout.NORTH);
        treePanel.add(new JBScrollPane(tree.getTreeTable()), BorderLayout.CENTER);

        BddTreeActionManager actionManager = new BddTreeActionManager(tree, Plugin.getInstance().getSessionManager());
        tree.setActionManager(actionManager);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAll(actionManager.getToolbarActions());

        panel.setContent(treePanel);
        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddTree.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());

        return content;
    }

    private static Content createConsoleContent(Project project) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, CONSOLE_LABEL, false);

        LauncherConsole console = new LauncherConsole(project, Plugin.getInstance().getSessionManager());

        ConsoleActionManager actionManager = new ConsoleActionManager(console);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAll(actionManager.getToolbarActions());

        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddConsole.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());

        panel.setContent(console.getComponent());

        return content;
    }

}


