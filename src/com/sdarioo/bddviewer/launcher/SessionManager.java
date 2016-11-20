package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SessionManager implements LauncherListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);

    private final AtomicBoolean isRunning = new AtomicBoolean();
    private final Set<Scenario> pending = ConcurrentHashMap.newKeySet();
    private final AtomicReference<Scenario> running = new AtomicReference<>();
    private final Map<Scenario, TestResult> finished = new ConcurrentHashMap<>();

    private final Launcher launcher;

    public SessionManager(Launcher launcher) {
        this.launcher = launcher;
        launcher.addListener(this);
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void scenarioStarted(Scenario scenario) {
        LOGGER.info("Scenario started: " + scenario.getName());
        running.set(scenario);
        pending.remove(scenario);
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
        LOGGER.info("Scenario finished: " + scenario.getName() + " Status: " + result.getStatus());
        running.set(null);
        finished.put(scenario, result);
    }

    @Override
    public void sessionStarted(List<Scenario> scope) {
        LOGGER.info("Session started.");
        clear();
        isRunning.set(true);
        pending.addAll(scope);
    }

    @Override
    public void sessionFinished() {
        LOGGER.info("Session finished.");
        isRunning.set(false);
    }

    @Override
    public void outputLine(String line) {
    }

    public boolean isPending(Scenario scenario) {
        return pending.contains(scenario);
    }

    public boolean isRunning(Scenario scenario) {
        return scenario.equals(running.get());
    }

    public TestResult getResult(Scenario scenario) {
        return finished.get(scenario);
    }

    public Collection<Scenario> getFinishedScenarios() {
        return finished.keySet();
    }

    public void clear() {
        if (isRunning()) {
            LOGGER.warn("Cannot clear session in progress.");
            return;
        }

        running.set(null);
        pending.clear();
        finished.clear();
    }
}
