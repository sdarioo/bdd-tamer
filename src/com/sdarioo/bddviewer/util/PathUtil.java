package com.sdarioo.bddviewer.util;

import java.nio.file.Path;

public final class PathUtil {

    private PathUtil() {}

    public static String getNameWithoutExtension(Path path) {
        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index > 0) {
            name = name.substring(0, index);
        }
        return name;
    }
}
