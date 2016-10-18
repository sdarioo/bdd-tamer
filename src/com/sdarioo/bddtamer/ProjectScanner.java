package com.sdarioo.bddtamer;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjectScanner {

    public static List<Path> scanForStoryFiles(Project project) {

        List<Path> result = new ArrayList<>();
        scan(project.getBaseDir(), result);
        return result;
    }


    private static void scan(VirtualFile file, List<Path> paths) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            VirtualFile[] children = file.getChildren();
            for (VirtualFile child : children) {
                scan(child, paths);
            }
        } else {
            String name = file.getName();
            if ((name != null) && name.endsWith(".story")) {
                String path = file.getCanonicalFile().getPath();
                paths.add(Paths.get(path));
            }
        }
    }
}
