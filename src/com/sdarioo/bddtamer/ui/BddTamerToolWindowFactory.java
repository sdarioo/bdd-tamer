package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.remoteServer.util.Column;
import com.intellij.ui.TreeTableSpeedSearch;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import com.intellij.ui.treeStructure.treetable.*;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.concurrent.ThreadLocalRandom;

//http://www.programcreek.com/java-api-examples/index.php?source_dir=platform_tools_adt_idea-master/android/src/com/android/tools/idea/editors/vmtrace/TraceViewPanel.java
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


        panel.setContent(createComponent4());
    }

    private JComponent createComponent4() {
        ListTreeTableModelOnColumns model = new ListTreeTableModelOnColumns(node(),
                new ColumnInfo[] { new Column("Name"), new Column("Status")}
        );
        TreeTable treeTable = new TreeTable(new BddTreeTableModel()) {
            @Override
            public TreeTableCellRenderer createTableRenderer(TreeTableModel treeTableModel) {
                TreeTableCellRenderer tableRenderer = super.createTableRenderer(treeTableModel);
                UIUtil.setLineStyleAngled(tableRenderer);
                tableRenderer.setRootVisible(false);
                tableRenderer.setShowsRootHandles(true);

                return tableRenderer;
            }
        };
        new TreeTableSpeedSearch(treeTable);
        treeTable.setRootVisible(true);
        treeTable.setProcessCursorKeys(true);


        return treeTable;
    }

    private static class Column extends ColumnInfo<TreeNode, String> {
        public Column(String name) {
            super(name);
        }
        @Override
        public String valueOf(TreeNode treeNode) {
            return treeNode.toString();
        }
    }

    private static DefaultTreeTableNode buildRoot ()
    {
        DefaultTreeTableNode root = new DefaultTreeTableNode(new Object(), false);
        DefaultTreeTableNode node1 = new DefaultTreeTableNode("Step 1. Open This Node", false);
        DefaultTreeTableNode subNode1 = new DefaultTreeTableNode("Ignore this node", false);
        node1.add(subNode1);
        subNode1.add(new DefaultTreeTableNode("SubNode1-1", false));
        subNode1 = new DefaultTreeTableNode("Step 2. Open This node and then Step 3. Click checkbox ->", false);
        node1.add(subNode1);
        subNode1.add(new DefaultTreeTableNode("SubNode2-1", false));
        root.add(node1);
        return root;
    }


//    private JComponent createContent3(){
//
//        TreeTableNode root = buildRoot();
//        //root.setValueAt(Object.class, 0);
//
//        TreeTable treeTable = new TreeTable(root);
//        treeTable.setRootVisible(false);
//        treeTable.setShowsRootHandles(true);
//
//        treeTable.setAutoCreateRowSorter(true);
//        ((DefaultTreeTableSorter)treeTable.getRowSorter()).setSortsOnUpdates(true);
//
//        //treeTable.setDragEnabled(true);
//        //treeTable.setAutoCreateRowHeader(true);
//
//        treeTable.setRowSelectionAllowed(true);
//
//        return new JBScrollPane(treeTable);
//    }

    private TreeNode node() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        root.add(new DefaultTreeTableNode("Child-1"));
        root.add(new DefaultTreeTableNode("Child-2"));
        return root;
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


