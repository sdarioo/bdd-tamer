package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddtamer.StoryProvider;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.DefaultTreeTableSorter;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;


public class BddTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTree.class);

    private final Project project;
    private final StoryProvider storyProvider;

    private TreeTable tree;

    public BddTree(Project project, StoryProvider storyProvider) {
        this.project = project;
        this.storyProvider = storyProvider;

        initializeUI();
    }

    public TreeTable getTreeTable() {
        return tree;
    }

    public void refresh() {
        DefaultTreeModel model = new DefaultTreeModel(buildRoot());
        tree.setTreeModel(model);
    }

    private void initializeUI() {
        DefaultTreeTableNode root = buildRoot();
        tree = new TreeTable(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setIconMap(new BddIconMap());

        tree.setAutoCreateRowSorter(true);
        ((DefaultTreeTableSorter)tree.getRowSorter()).setSortsOnUpdates(true);

        tree.setDragEnabled(false);
        tree.setAutoCreateRowHeader(false);
        tree.setRowSelectionAllowed(false);

        new BddTreeSpeedSearch(tree);
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
        return new DefaultTreeTableNode(getRowData(modelObject));
    }

    private static Object[] getRowData(Object modelObject) {

        Object requirement = "";
        if (modelObject instanceof Scenario) {
            requirement = ((Scenario)modelObject).getMeta().getRequirements();
        }
        return new Object[] { modelObject, requirement };
    }



}
