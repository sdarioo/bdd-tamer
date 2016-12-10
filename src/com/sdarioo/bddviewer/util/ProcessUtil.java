package com.sdarioo.bddviewer.util;

import com.intellij.openapi.diagnostic.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ProcessUtil {
    private static final Logger LOGGER = Logger.getInstance(ProcessUtil.class);

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "Process stream reader thread.");
        t.setDaemon(true);
        return t;
    });


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

    /**
     * Execute given command line and return running process. This method doesn't wait for process termination.
     * Call process.waitFor() to wait for running process termination.
     */
    public static Process exec(String[] command, File runDir, Consumer<String> out, Consumer<String> err)
        throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command, null, runDir);

        readStreamAsync(process.getInputStream(), out);
        readStreamAsync(process.getErrorStream(), err);
        return process;
    }

    private static void readStreamAsync(InputStream inputStream, Consumer<String> consumer) {
        EXECUTOR.submit(() -> {
            String line;
            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = stdError.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                consumer.accept("Error reading process output: " + e.toString());
            }
        });
    }


    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1);
    }
}
