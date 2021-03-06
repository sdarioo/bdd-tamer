package com.sdarioo.bddviewer.ui;

import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionContext;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.console.Console;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;
import com.sdarioo.bddviewer.ui.console.actions.ConsoleActionManager;
import com.sdarioo.bddviewer.ui.tree.BddTree;
import com.sdarioo.bddviewer.ui.tree.actions.BddTreeActionManager;
import com.sdarioo.bddviewer.ui.tree.search.SearchComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class BddToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOGGER = Logger.getInstance(BddToolWindowFactory.class);

    /** IMPORTANT: make sure that this value is in sync with tool window id registered in plugin.xml */
    public static final String TOOL_WINDOW_ID = "BDD";

    public static final String TREE_CONTENT_ID = "Tree";
    public static final String CONSOLE_CONTENT_ID = "Console";

    private static final String TOOL_WINDOW_ICON_KEY = "ToolWindow.Icon";
    private static final String TREE_CONTENT_ICON_KEY = "TreeContent.Icon";

    private static final Key<BddTree> TREE_KEY = Key.create(TREE_CONTENT_ID);
    private static final Key<LauncherConsole> CONSOLE_KEY = Key.create(CONSOLE_CONTENT_ID);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        BddTree tree = new BddTree(project);
        LauncherConsole console = new LauncherConsole(project);

        Content treeContent = createTreeContent(project, tree, console);
        treeContent.putUserData(TREE_KEY, tree);

        Content consoleContent = createConsoleContent(project, console);
        consoleContent.putUserData(CONSOLE_KEY, console);

        toolWindow.getContentManager().addContent(treeContent);
        toolWindow.getContentManager().addContent(consoleContent);

        Plugin.getInstance().getLauncher(project).addListener(new LauncherListenerAdapter() {
            @Override
            public void sessionStarted(List<Scenario> scope, SessionContext context) {

                UIUtil.invokeLaterIfNeeded(() -> {
                    toolWindow.getContentManager().setSelectedContent(consoleContent);

                    Icon icon = toolWindow.getIcon();
                    context.addProperty(TOOL_WINDOW_ICON_KEY, icon);
                    toolWindow.setIcon(ExecutionUtil.getLiveIndicator(icon));

                    icon = treeContent.getIcon();
                    context.addProperty(TREE_CONTENT_ICON_KEY, icon);
                    treeContent.setIcon(ExecutionUtil.getLiveIndicator(icon));
                });
            }

            @Override
            public void sessionFinished(SessionContext context) {
                UIUtil.invokeLaterIfNeeded(() -> {
                    Icon icon = (Icon)context.getProperty(TOOL_WINDOW_ICON_KEY);
                    toolWindow.setIcon(icon);

                    icon = (Icon)context.getProperty(TREE_CONTENT_ICON_KEY);
                    treeContent.setIcon(icon);
                });
            }
        });
    }

    /**
     * @param project current project
     * @return console
     */
    public static Console getConsole(Project project) {
        ToolWindow toolWindow = getToolWindow(project);
        if (toolWindow == null) {
            return null;
        }
        Content content = toolWindow.getContentManager().findContent(CONSOLE_CONTENT_ID);
        return (content != null) ? content.getUserData(CONSOLE_KEY) : null;
    }

    public static BddTree getTree(Project project) {
        ToolWindow toolWindow = getToolWindow(project);
        if (toolWindow == null) {
            return null;
        }
        Content content = toolWindow.getContentManager().findContent(TREE_CONTENT_ID);
        return (content != null) ? content.getUserData(TREE_KEY) : null;
    }

    /**
     * Select BDD tool window content with given identifier.
     * @param project current project
     * @param contentId content identifier
     */
    public static void selectContent(Project project, String contentId) {
        ToolWindow toolWindow = getToolWindow(project);
        if (toolWindow == null) {
            return;
        }
        Content content = toolWindow.getContentManager().findContent(contentId);
        if (content != null) {
            toolWindow.getContentManager().setSelectedContent(content);
        } else {
            LOGGER.warn("Cannot find Content:" + contentId);
        }
    }

    private static ToolWindow getToolWindow(Project project) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = manager.getToolWindow(BddToolWindowFactory.TOOL_WINDOW_ID);
        if (toolWindow == null) {
            LOGGER.warn("Cannot find ToolWindow: " + BddToolWindowFactory.TOOL_WINDOW_ID);
        }
        return toolWindow;
    }

    private static Content createTreeContent(Project project, BddTree tree, LauncherConsole console) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, TREE_CONTENT_ID, false);

        JPanel treePanel = new JPanel(new BorderLayout());
        SearchComponent searchPanel = new SearchComponent(treePanel, tree.getTreeTable());
        treePanel.add(searchPanel.getComponent(), BorderLayout.NORTH);
        treePanel.add(new JBScrollPane(tree.getTreeTable()), BorderLayout.CENTER);

        content.setPreferredFocusableComponent(tree.getTreeTable());
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setIcon(AllIcons.Actions.ShowAsTree);

        BddTreeActionManager actionManager = new BddTreeActionManager(project, tree, console);
        tree.setActionManager(actionManager);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAll(actionManager.getToolbarActions());

        panel.setContent(treePanel);
        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddTree.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());

        return content;
    }

    private static Content createConsoleContent(Project project, LauncherConsole console) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, CONSOLE_CONTENT_ID, false);

        ConsoleActionManager actionManager = new ConsoleActionManager(console);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAll(actionManager.getToolbarActions());

        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddConsole.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());

        panel.setContent(console.getComponent());

        return content;
    }

}


