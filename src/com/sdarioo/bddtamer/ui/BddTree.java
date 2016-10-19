package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddtamer.StoryProvider;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import de.sciss.treetable.j.DefaultTreeColumnModel;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.DefaultTreeTableSorter;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;


public class BddTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTree.class);
    private static final String[] COLUMNS = { "Name", "Requirement" };

    private final Project project;
    private final StoryProvider storyProvider;

    private TreeTable tree;
    private FilteredTreeModel treeModel;


    public BddTree(Project project, StoryProvider storyProvider) {
        this.project = project;
        this.storyProvider = storyProvider;

        initializeUI();
    }

    public TreeTable getTreeTable() {
        return tree;
    }

    public void refresh() {
        DefaultTreeTableNode root = buildRoot();
        treeModel = createTreeModel(root);
        tree.setTreeModel(treeModel);
    }

    public void search(String text) {
        treeModel.setFilter(text);
        tree.setTreeModel(treeModel);


        //((DefaultTreeModel)treeModel.getTreeModel()).reload();
    }

    private void initializeUI() {
        DefaultTreeTableNode root = buildRoot();
        treeModel = createTreeModel(root);
        DefaultTreeColumnModel columnModel = new DefaultTreeColumnModel(root, COLUMNS);
        columnModel.setAllColumnsEditable(false);

        tree = new TreeTable(treeModel, columnModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setIconMap(new BddIconMap());

        tree.setAutoCreateRowSorter(true);
        ((DefaultTreeTableSorter)tree.getRowSorter()).setSortsOnUpdates(true);

        tree.setDragEnabled(false);
        tree.setAutoCreateRowHeader(false);
        tree.setRowSelectionAllowed(true);
        tree.setCellSelectionEnabled(false);
        tree.setColumnFocusEnabled(false);

        // Right click should select row
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());

                    int row = tree.rowAtPoint(e.getPoint());
                    int col = tree.columnAtPoint(e.getPoint());
                    if ((row >= 0) && (col >= 0)) {
                        tree.setSelectionRow(row);
                    }


//                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//                    tree.setSelectionPath(selPath);
//                    if (selRow > -1) {
//                        tree.setSelectionRow(selRow);
//                    }
                }
            }
        };
        tree.addMouseListener(ml);

        new BddTreeSpeedSearch(tree);
    }

    private FilteredTreeModel createTreeModel(DefaultTreeTableNode root) {
        return new FilteredTreeModel(new DefaultTreeModel(root));
    }

    private DefaultTreeTableNode buildRoot()
    {
        DefaultTreeTableNode root = createNode(project);

        List<Story> stories = storyProvider.getStories(project);
        stories.forEach(story -> {
            DefaultTreeTableNode storyNode = createNode(story);
            root.add(storyNode);
            story.getScenarios().forEach(scenario -> storyNode.add(createNode(scenario)));
        });
        return root;
    }

    private static DefaultTreeTableNode createNode(Object modelObject) {
        DefaultTreeTableNode node = new DefaultTreeTableNode(getRowData(modelObject));
        node.setUserObject(modelObject);
        return node;
    }

    private static Object[] getRowData(Object modelObject) {

        Object requirement = "";
        if (modelObject instanceof Scenario) {
            requirement = ((Scenario)modelObject).getMeta().getRequirements();
        }
        return new Object[] { modelObject, requirement };
    }



}
