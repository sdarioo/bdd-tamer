package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DummyLauncher extends AbstractLauncher {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean isRunning;

    @Override
    public void submit(List<Scenario> scenarios) throws LauncherException {
        throwIfRunning();
        executorService.submit(() -> {
            execute(scenarios);
        });
    }

    private void execute(List<Scenario> scenarios) {
        isRunning = true;
        try {
            notifySessionStarted(scenarios);
            for (Scenario scenario : scenarios) {
                notifyTestStarted(scenario);
                try {Thread.sleep(500);} catch (InterruptedException e) {}
                notifyTestFinished(scenario, new TestResult(RunStatus.Passed, 1000, "No output"));
            }
        } finally {
            notifySessionFinished();
            isRunning = false;
        }
    }

    protected void throwIfRunning() throws LauncherException {
        if (isRunning) {
            throw new LauncherException("There is other run in progress.");
        }
    }
}
