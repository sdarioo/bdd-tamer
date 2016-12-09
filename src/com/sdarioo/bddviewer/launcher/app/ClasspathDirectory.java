package com.sdarioo.bddviewer.launcher.app;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClasspathDirectory {

    private final Path directory;

    public ClasspathDirectory(Path directory) {
        this.directory = directory;
    }

    /**
     * @return set of qualified class names within this classpath directory
     */
    public Set<String> listClasses() {
        return listClasses(directory.toFile());
    }

    public String findClassWithName(String name) {
        for (String clazz : listClasses()) {
            if (clazz.endsWith('.' + name)) {
                return clazz;
            }
        }
        return null;
    }

    public Class<?> loadClassWithName(String name) {
        String qName = findClassWithName(name);
        try {
            return Class.forName(qName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to load class: " + name, e);
        }
    }

    private static Set<String> listClasses(File dir) {
        if (!dir.isDirectory()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for (String name : dir.list()) {
            File child = new File(dir, name);
            if (child.isDirectory()) {
                Set<String> childList = listClasses(child);
                result.addAll(childList.stream().map(n -> name + '.' + n).collect(Collectors.toList()));
            }
            if (child.isFile() && child.getName().endsWith(".class")) {
                result.add(name.substring(0, name.length() - ".class".length()));
            }
        }
        return result;
    }
}
