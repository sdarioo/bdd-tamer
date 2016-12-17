package com.sdarioo.bddviewer.ui;


import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.ui.console.Console;

/**
 * Provides output console
 */
public interface ConsoleProvider {

    /**
     * @param project
     * @return console instance for given project
     */
    Console getConsole(Project project);
}
