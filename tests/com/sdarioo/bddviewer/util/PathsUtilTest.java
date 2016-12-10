package com.sdarioo.bddviewer.util;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PathsUtilTest {


    @Test
    public void tempDirExists() throws Exception {
        assertTrue(Files.isDirectory(PathUtil.TEMP_DIR));
    }

    @Test
    public void getCommonRoot() throws Exception {

        Path root = Paths.get("c:");
        List<Path> paths = Collections.singletonList(root);
        assertEquals(root, PathUtil.findCommonRoot(paths));
    }

    @Test
    public void noCommonRoot() throws Exception {

        Path root1 = Paths.get("c:\\");
        Path root2 = Paths.get("d:\\");
        List<Path> paths = Arrays.asList(root1, root2);
        assertNull(PathUtil.findCommonRoot(paths));
    }

    @Test
    public void sameDirCommonRoot() throws Exception {

        Path path1 = Paths.get("c:\\temp\\dir\\f1.txt");
        Path path2 = Paths.get("c:\\temp\\dir\\f2.txt");
        List<Path> paths = Arrays.asList(path1, path2);
        assertEquals(Paths.get("c:\\temp\\dir"), PathUtil.findCommonRoot(paths));
    }

    @Test
    public void commonRoot() throws Exception {

        Path path1 = Paths.get("c:\\temp1\\dir\\f1.txt");
        Path path2 = Paths.get("c:\\temp2\\f2.txt");
        List<Path> paths = Arrays.asList(path1, path2);
        assertEquals(Paths.get("c:\\"), PathUtil.findCommonRoot(paths));
    }

    @Test
    public void split() {
        assertArrayEquals(new String[0], PathUtil.split(Paths.get("")));
        assertArrayEquals(new String[]{"c:"}, PathUtil.split(Paths.get("c:\\")));
        assertArrayEquals(new String[]{"a","b","c"}, PathUtil.split(Paths.get("a/b/c")));
        assertArrayEquals(new String[]{"a","b","c"}, PathUtil.split(Paths.get("a\\b\\c")));
        assertArrayEquals(new String[]{"c:","a","b","c"}, PathUtil.split(Paths.get("c:/a\\b\\c")));
    }
}
