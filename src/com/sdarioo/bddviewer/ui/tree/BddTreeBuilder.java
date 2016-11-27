package com.sdarioo.bddviewer.ui.tree;


import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.model.Story;
import com.sdarioo.bddviewer.provider.StoryProvider;
import com.sdarioo.bddviewer.util.PathUtil;
import de.sciss.treetable.j.DefaultTreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BddTreeBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BddTreeBuilder.class);

    private final Project project;
    private final StoryProvider storyProvider;
    private final BddTreeColumns columns;

    public BddTreeBuilder(Project project, StoryProvider storyProvider, BddTreeColumns columns) {
        this.project = project;
        this.columns = columns;
        this.storyProvider = storyProvider;
    }

    public DefaultTreeTableNode buildTree() {
        DefaultTreeTableNode root = createNode(project);
        buildTreeStructure(root);
        return root;
    }

    public DefaultTreeTableNode createNode(Object modelObject) {
        Object[] rowData = columns.getColumnValues(modelObject).toArray();
        DefaultTreeTableNode node = new DefaultTreeTableNode(rowData);
        node.setUserObject(modelObject);
        return node;
    }

    public void buildTreeStructure(DefaultTreeTableNode rootNode) {
        List<Story> stories = storyProvider.getStories(project);
        if (stories.isEmpty()) {
            return;
        }
        sortStories(stories);
        List<Path> storyPaths = stories.stream()
                .map(s -> s.getLocation().getPath())
                .collect(Collectors.toList());

        Path projectDir = Paths.get(project.getBasePath());
        Path commonRootDir = PathUtil.findCommonRoot(storyPaths);
        if ((commonRootDir == null) || !commonRootDir.startsWith(projectDir)) {
            commonRootDir = projectDir;
        }
        Map<Path, DefaultTreeTableNode> dirNodes = new HashMap<>();

        for (Story story : stories) {
            Path storyDir = story.getLocation().getPath().getParent();
            if (!storyDir.startsWith(commonRootDir)) {
                LOGGER.warn("Story path " + storyDir + " is not part of root path: " + commonRootDir);
                continue;
            }
            Path parentDir = commonRootDir;
            DefaultTreeTableNode parentNode = rootNode;
            Path storyRelPath = commonRootDir.relativize(storyDir);
            for (String segment : PathUtil.split(storyRelPath)) {
                parentDir = parentDir.resolve(segment);
                DefaultTreeTableNode nextParentNode = dirNodes.get(parentDir);
                if (nextParentNode == null) {
                    nextParentNode = createNode(Paths.get(segment));
                    dirNodes.put(parentDir, nextParentNode);
                    parentNode.add(nextParentNode);
                }
                parentNode = nextParentNode;
            }
            DefaultTreeTableNode storyNode = createNode(story);
            parentNode.add(storyNode);
            story.getScenarios().forEach(scenario -> storyNode.add(createNode(scenario)));
        }
    }

    /**
     * Sort alphabetically but keep directories first
     * @param stories
     */
    private void sortStories(List<Story> stories) {
        stories.sort((s1, s2) -> {
            Path p1 = s1.getLocation().getPath();
            Path p2 = s2.getLocation().getPath();
            int count1 = p1.getNameCount();
            int count2 = p2.getNameCount();
            if (count1 == count2) {
                return p1.compareTo(p2);
            }
            String[] segments1 = PathUtil.split(p1.getParent());
            String[] segments2 = PathUtil.split(p2.getParent());
            for (int i = 0; i < Math.min(segments1.length, segments2.length); i++) {
                int compare = segments1[i].compareTo(segments2[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return (count1 > count2) ? -1 : 1;
        });
    }
}

