package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jetbrains.annotations.NotNull;


import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;


public class BddTamerToolWindowFactory implements ToolWindowFactory {

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


        panel.setContent(createContent2());
    }

    // https://github.com/Sciss/TreeTable
    private JComponent createContent2() {
        return new JTree();
    }

    private JComponent createContent1() {
        DefaultTreeTableModel model = new DefaultTreeTableModel(BddTreeTableModel.getDefaultRoot(), Arrays.asList("Name", "Status"));
        JXTreeTable tree = new JXTreeTable(model);
        tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = tree.rowAtPoint(e.getPoint());
                if (r >= 0 && r < tree.getRowCount()) {
                    tree.setRowSelectionInterval(r, r);
                } else {
                    tree.clearSelection();
                }

                int rowindex = tree.getSelectedRow();
                if (rowindex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JXTreeTable ) {
                    //JPopupMenu popup = createYourPopUp();
                    //popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        return tree;
    }

    private static class DemoAction extends AnAction {

        DemoAction() {
            super("REFRESH");
            setDefaultIcon(true);
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {

        }
    }

}
