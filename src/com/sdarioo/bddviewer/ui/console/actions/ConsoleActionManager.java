package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.sdarioo.bddviewer.ui.console.OutputConsole;

import java.util.Arrays;
import java.util.List;

public class ConsoleActionManager {

    private final OutputConsole console;

    private final ClearAction clearAction;

    public ConsoleActionManager(OutputConsole console) {
        this.console = console;
        this.clearAction = new ClearAction(console);
    }

    public List<AnAction> getToolbarActions() {
        return Arrays.asList(clearAction);
    }
}
