package com.sdarioo.bddtamer.ui;

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
import com.sdarioo.bddtamer.ProjectScannerStoryProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//http://www.programcreek.com/java-api-examples/index.php?source_dir=platform_tools_adt_idea-master/android/src/com/android/tools/idea/editors/vmtrace/TraceViewPanel.java
public class BddToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddToolWindowFactory.class);

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, false);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setIcon(AllIcons.Toolwindows.Documentation);

        BddTreeView bddTreeView = new BddTreeView(project, new ProjectScannerStoryProvider());
        panel.setContent(bddTreeView.getComponent());

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        BddActionManager actionManager = bddTreeView.getActionManager();
        actionManager.getToolbarAction().forEach(a -> actionGroup.add(a));

        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("bddTree.Toolbar", actionGroup, false);
        panel.setToolbar(toolBar.getComponent());
    }

}


