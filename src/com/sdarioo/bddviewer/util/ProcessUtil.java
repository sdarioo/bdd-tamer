package com.sdarioo.bddviewer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

public class ProcessUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtil.class);

    public static void kill(Process process, String cmdLineSubstring) {
        if (isWindows()) {
            exec("wmic process Where \"CommandLine Like '%" + cmdLineSubstring + "%'\" Call Terminate");
        } else {
            int pid = getUnixPid(process);
            if (pid > 0) {
                exec("kill -9 " + pid);
            }
        }
    }

    public static int getUnixPid(Process proc) {
        // UNIXProcess
        try {
            Field f = proc.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            return (Integer)f.get(proc);
        } catch (Throwable t) { /* ignore */ }

        return -1;
    }

    private static void exec(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            LOGGER.warn("Command failed: " + cmd, e);
        }
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1);
    }
}
