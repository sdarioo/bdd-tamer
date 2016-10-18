package com.sdarioo.bddtamer.ui;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddtamer.ProjectScanner;
import com.sdarioo.bddtamer.StoryParser;
import com.sdarioo.bddtamer.model.Scenario;
import com.sdarioo.bddtamer.model.Story;
import de.sciss.treetable.j.DefaultTreeTableNode;
import de.sciss.treetable.j.DefaultTreeTableSorter;
import de.sciss.treetable.j.TreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BddTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTree.class);

    public static TreeTable createTree(Project project) {

        TreeTable tree = new TreeTable(buildRoot(project));
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        tree.setAutoCreateRowSorter(true);
        ((DefaultTreeTableSorter)tree.getRowSorter()).setSortsOnUpdates(true);

        tree.setDragEnabled(false);
        tree.setAutoCreateRowHeader(false);
        tree.setRowSelectionAllowed(false);

        new BddTreeSpeedSearch(tree);

        return tree;
    }

    private static DefaultTreeTableNode buildRoot(Project project)
    {
        DefaultTreeTableNode root = new DefaultTreeTableNode("BDD", "");

        List<Path> storyFiles = ProjectScanner.scanForStoryFiles(project);
        for (Path path : storyFiles) {
            try {
                Story story = StoryParser.parse(path);
                DefaultTreeTableNode storyNode = new DefaultTreeTableNode(story.getName(), "");
                root.add(storyNode);

                for (Scenario scenario : story.getScenarios()) {
                    storyNode.add(new DefaultTreeTableNode(scenario.getName(), scenario.getMeta().getRequirements()));
                }
            } catch (IOException e) {
                LOGGER.error("Error parsing story file: " + path.toString() + " Error: " + e.toString());
            }
        }
        return root;
    }

}
