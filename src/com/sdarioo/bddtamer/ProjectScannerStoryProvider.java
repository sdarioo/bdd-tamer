package com.sdarioo.bddtamer;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sdarioo.bddtamer.model.Story;
import com.sdarioo.bddtamer.parser.StoryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectScannerStoryProvider implements StoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectScannerStoryProvider.class);

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
        if (file.isDirectory()) {
            VirtualFile[] children = file.getChildren();
            for (VirtualFile child : children) {
                recursiveFindPaths(child, paths);
            }
        } else if (isStoryFile(file)) {
            String path = file.getCanonicalPath();
            paths.add(Paths.get(path));
        }
    }

    private static boolean isStoryFile(VirtualFile file) {
        String name = file.getName();
        return (name != null) && name.endsWith(".story");
    }

}
