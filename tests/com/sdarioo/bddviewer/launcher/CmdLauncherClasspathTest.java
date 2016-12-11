package com.sdarioo.bddviewer.launcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CmdLauncherClasspathTest {

    @Test
    public void testStripVersion() throws Exception {

        assertEquals("a.jar", CmdLauncherClasspath.stripVersion("a.jar"));
        assertEquals("a-test.jar", CmdLauncherClasspath.stripVersion("a-test.jar"));
        assertEquals("a.jar", CmdLauncherClasspath.stripVersion("a-0.0.1.jar"));
        assertEquals("a.jar", CmdLauncherClasspath.stripVersion("a-01.01.01-SNAPSHOT.jar"));
        assertEquals("a-tests.jar", CmdLauncherClasspath.stripVersion("a-01.01.01-SNAPSHOT-tests.jar"));
        assertEquals("a-sources.jar", CmdLauncherClasspath.stripVersion("a-01.01.01-SNAPSHOT-sources.jar"));
    }

}
