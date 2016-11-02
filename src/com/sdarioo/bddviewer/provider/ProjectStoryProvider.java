package com.sdarioo.bddviewer.provider;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sdarioo.bddviewer.model.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * StoryProvider implementation that scans whole project for .story files.
 */
public class ProjectStoryProvider implements StoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStoryProvider.class);

    @Override
    public List<Story> getStories(Project project) {
        List<Path> paths = new ArrayList<>();
        recursiveFindPaths(project.getBaseDir(), paths);
        LOGGER.info("Found " + paths.size() + " story files.");

        List<Story> result = new ArrayList<>(paths.size());
        for (Path path : paths) {
            try {
                Story story = StoryParser.parse(path);
                result.add(story);
            } catch (IOException e) {
                LOGGER.error("Error parsing story file: " + path.toString() + " Error: " + e.toString());
            }
        }
        return result;
    }

    private static void recursiveFindPaths(VirtualFile file, List<Path> paths) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory() && !"target".equals(file.getName())) {
            VirtualFile[] children = file.getChildren();
            for (VirtualFile child : children) {
                recursiveFindPaths(child, paths);
            }
        } else if (isStoryFile(file)) {
            String path = file.getCanonicalPath();
            paths.add(Paths.get(path));
        }
    }

    public static boolean isStoryFile(VirtualFile file) {
        String name = file.getName();
        return (name != null) && name.endsWith(".story");
    }

}
