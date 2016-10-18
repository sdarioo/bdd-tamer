package com.sdarioo.bddtamer.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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


        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new DemoAction());
        actionGroup.addSeparator();

        ActionManager instance = ActionManager.getInstance();
        ActionToolbar actionToolbar = instance.createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true);
        panel.setToolbar(actionToolbar.getComponent());

        BddTreeView bddTreeView = new BddTreeView(project, new ProjectScannerStoryProvider());
        panel.setContent(bddTreeView.getComponent());
    }

    private static class DemoAction extends AnAction {

        DemoAction() {
            super("REFRESH", "text", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {

        }
    }


}


