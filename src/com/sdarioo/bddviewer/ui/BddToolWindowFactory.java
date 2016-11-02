package com.sdarioo.bddviewer.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.sdarioo.bddviewer.Plugin;
import com.sdarioo.bddviewer.ui.actions.BddActionManager;
import com.sdarioo.bddviewer.ui.console.OutputConsole;
import com.sdarioo.bddviewer.ui.tree.BddTreeView;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

        BddTreeView bddTreeView = new BddTreeView(project,
                Plugin.getInstance().getStoryProvider(),
                Plugin.getInstance().getSessionManager());

        panel.setContent(bddTreeView.getComponent());

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        BddActionManager actionManager = bddTreeView.getActionManager();
        actionManager.getToolbarActions().forEach(a -> actionGroup.add(a));

        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddTree.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());

        return content;
    }

    private static Content createConsoleContent(Project project) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, CONSOLE_LABEL, false);

        OutputConsole console = new OutputConsole(Plugin.getInstance().getSessionManager());
        panel.setContent(console.getComponent());

        return content;
    }

}


