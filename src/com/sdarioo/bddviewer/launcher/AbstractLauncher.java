package com.sdarioo.bddviewer.launcher;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.ui.console.Console;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public abstract class AbstractLauncher implements Launcher {

    protected final Project project;
    protected final Function<Project, Console> consoleProvider;

    private final AtomicBoolean isRunning = new AtomicBoolean();
    private final List<LauncherListener> listeners = new CopyOnWriteArrayList<>();

    private LauncherOutputFormatter outputFormatter;

    protected AbstractLauncher(Project project, Function<Project, Console> consoleProvider) {
        this.consoleProvider = consoleProvider;
        this.project = project;
    }

    @Override
    public void submit(List<Scenario> scenarios) throws LauncherException {
        if (!isRunning.compareAndSet(false, true)) {
            throw new LauncherException("There is other run in progress.");
        }
        outputFormatter = createOutputFormatter(consoleProvider.apply(project));
        addListener(outputFormatter);

        SessionContext context = new SessionContext();
        notifySessionStarted(scenarios, context);

        executeAsync(scenarios, () -> {
            isRunning.set(false);
            notifySessionFinished(context);

            removeListener(outputFormatter);
            outputFormatter = null;
        });
    }

    protected void executeAsync(List<Scenario> scenarios, Runnable finishCallback) {
        CompletableFuture
                .runAsync(() -> executeAll(scenarios))
                .thenRun(finishCallback);
    }

    protected abstract LauncherOutputFormatter createOutputFormatter(Console console);

    protected abstract void executeAll(List<Scenario> scenario);

    @Override
    public void addListener(LauncherListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(LauncherListener listener) {
        listeners.remove(listener);
    }

    protected void notifySessionStarted(List<Scenario> scope, SessionContext context) {
        listeners.forEach(l -> l.sessionStarted(scope, context));
    }

    protected void notifySessionFinished(SessionContext context) {
        listeners.forEach(l -> l.sessionFinished(context));
    }

    protected void notifyTestStarted(Scenario scenario) {
        listeners.forEach(l -> l.scenarioStarted(scenario));
    }

    protected void notifyTestFinished(Scenario scenario, TestResult result) {
        listeners.forEach(l -> l.scenarioFinished(scenario, result));
    }

    protected void notifyInfo(String line) {
        outputFormatter.outputLine(line, LauncherOutputFormatter.Severity.Info);
    }

    protected void notifyError(String line) {
        outputFormatter.outputLine(line, LauncherOutputFormatter.Severity.Error);
    }

    protected void notifyOutput(String line) {
        outputFormatter.outputLine(line, LauncherOutputFormatter.Severity.Normal);
    }
}
