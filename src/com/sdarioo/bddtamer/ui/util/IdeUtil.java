package com.sdarioo.bddtamer.ui.util;


import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.sdarioo.bddtamer.model.Location;

import java.nio.file.Path;

public class IdeUtil {

    private IdeUtil() {}

    /**
     * Open file represented by given path in Intellij editor view.
     * @param project opened project
     * @param location file path to open
     */
    public static void openInEditor(Project project, Location location) {

        Path path = location.getPath();
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(path.toFile());
        OpenFileDescriptor loc = new OpenFileDescriptor(project, file, location.getStartLine() - 1, 0);
        FileEditorManager manager = FileEditorManager.getInstance(project);
        manager.openTextEditor(loc, true);
    }
}
