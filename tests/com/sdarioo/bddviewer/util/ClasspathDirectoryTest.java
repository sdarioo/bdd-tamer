package com.sdarioo.bddviewer.util;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.*;

public class ClasspathDirectoryTest {

    @Test
    public void listClassesInvalidDir() throws Exception {
        Path invalid = Paths.get("invalid");
        ClasspathDirectory dir = new ClasspathDirectory(invalid);
        Set<String> classes = dir.listClasses();
        assertNotNull(classes);
        assertTrue(classes.isEmpty());
    }

    @Test
    public void listClassesTempDir() throws Exception {

        Path temp = PathUtil.TEMP_DIR.resolve("listClassesTempDir");
        Files.createDirectory(temp);
        try {
            ClasspathDirectory dir = new ClasspathDirectory(temp);
            Set<String> classes = dir.listClasses();
            assertNotNull(classes);
            assertTrue(classes.isEmpty());
        } finally {
            FileUtil.deleteDir(temp);
            assertFalse(Files.isDirectory(temp));
        }
    }

    @Test
    public void listClasses() throws Exception {
        Path temp = PathUtil.TEMP_DIR.resolve("listClasses");
        Files.createDirectory(temp);
        try {
            Path pkg = temp.resolve("a/b/c");
            Files.createDirectories(pkg);
            Files.createFile(pkg.resolve("A.class"));
            Files.createFile(pkg.resolve("A$B.class"));
            Files.createFile(pkg.resolve("B.class"));

            ClasspathDirectory dir = new ClasspathDirectory(temp);
            Set<String> classes = dir.listClasses();
            assertNotNull(classes);
            assertEquals(3, classes.size());

            assertTrue(classes.contains("a.b.c.A"));
            assertTrue(classes.contains("a.b.c.B"));
            assertTrue(classes.contains("a.b.c.A$B"));

            assertEquals("a.b.c.A", dir.findClassWithName("A"));

        } finally {
            FileUtil.deleteDir(temp);
            assertFalse(Files.isDirectory(temp));
        }
    }
}
