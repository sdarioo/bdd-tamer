package com.sdarioo.bddviewer.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProcessUtilTest {

    @Test
    public void exec() throws Exception {
        Process proc = ProcessUtil.exec(new String[] {"java", "-version"}, null, System.out::println, System.err::println);
        int code = proc.waitFor();
        assertEquals(0, code);
    }
}
