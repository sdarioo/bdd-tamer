package com.sdarioo.bddviewer.launcher;

/**
 * Launcher output formatter
 */
public interface LauncherOutputFormatter extends LauncherListener {

    void outputLine(String line, Severity severity);

    enum Severity {
        Normal,
        Info,
        Error
    }
}
