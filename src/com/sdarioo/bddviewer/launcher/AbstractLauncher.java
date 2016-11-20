package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractLauncher implements Launcher {

    private final AtomicBoolean isRunning = new AtomicBoolean();
    private final List<LauncherListener> listeners = new CopyOnWriteArrayList<>();

    protected AbstractLauncher() {
    }

    @Override
    public void submit(List<Scenario> scenarios) throws LauncherException {
        if (!isRunning.compareAndSet(false, true)) {
            throw new LauncherException("There is other run in progress.");
        }
        notifySessionStarted(scenarios);

        executeAsync(scenarios, () -> {
            isRunning.set(false);
            notifySessionFinished();
        });
    }

    protected void executeAsync(List<Scenario> scenarios, Runnable finishCallback) {

        CompletableFuture.runAsync( () -> {
            for (Scenario scenario : scenarios) {
                notifyTestStarted(scenario);
                TestResult result = null;
                try {
                    //if (scenario.isRunnable()) {
                        result = execute(scenario);
                    //} else {
                    //    result = new TestResult(RunStatus.Skipped, 0L, "Skipping: " + scenario.getName());
                    //}
                } catch (Throwable t) {
                    result = new TestResult(RunStatus.Failed, 0L, "Error: " + t.toString());
                } finally {
                    notifyTestFinished(scenario, result);
                }
            }
        }).thenRun(finishCallback);
    }

    protected abstract TestResult execute(Scenario scenario);

    @Override
    public void addListener(LauncherListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(LauncherListener listener) {
        listeners.remove(listener);
    }

    protected void notifySessionStarted(List<Scenario> scope) {
        listeners.forEach(l -> l.sessionStarted(scope));
    }

    protected void notifySessionFinished() {
        listeners.forEach(LauncherListener::sessionFinished);
    }

    protected void notifyTestStarted(Scenario scenario) {
        listeners.forEach(l -> l.scenarioStarted(scenario));
    }

    protected void notifyTestFinished(Scenario scenario, TestResult result) {
        listeners.forEach(l -> l.scenarioFinished(scenario, result));
    }

    protected void notifyOutputLine(String line) {
        listeners.forEach(l -> l.outputLine(line));
    }

}
