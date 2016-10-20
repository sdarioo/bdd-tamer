package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractLauncher implements Launcher {

    private final List<LauncherListener> listeners = new CopyOnWriteArrayList<>();

    private volatile boolean isRunning = false;

    protected AbstractLauncher() {
    }

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
}
