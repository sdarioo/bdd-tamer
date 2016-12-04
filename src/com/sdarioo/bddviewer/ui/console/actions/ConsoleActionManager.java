package com.sdarioo.bddviewer.ui.console.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.sdarioo.bddviewer.ui.console.LauncherConsole;

import java.util.Arrays;
import java.util.List;

public class ConsoleActionManager {

    private final ClearAction clearAction;
    private final VerboseOutputAction verboseOutputAction;
    private final CompactOutputAction compactOutputAction;

    public ConsoleActionManager(LauncherConsole console) {
        this.clearAction = new ClearAction(console);
        this.verboseOutputAction = new VerboseOutputAction(console);
        this.compactOutputAction = new CompactOutputAction(console);
    }

    public List<AnAction> getToolbarActions() {
        return Arrays.asList(clearAction, verboseOutputAction, compactOutputAction);
    }
}
