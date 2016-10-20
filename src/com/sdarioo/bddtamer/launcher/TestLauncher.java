package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestLauncher extends AbstractLauncher {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final TestLauncher INSTANCE = new TestLauncher();

    private TestLauncher() {}

    public static TestLauncher getInstance() {
        return INSTANCE;
    }


    @Override
    public void submit(List<Scenario> scenarios) {
        executorService.submit(() -> {
            execute(scenarios);
        });
    }

    private void execute(List<Scenario> scenarios) {
        notifySessionStarted();
        try {
            for (Scenario scenario : scenarios) {
                notifyTestStarted(scenario);
                try {Thread.sleep(500);} catch (InterruptedException e) {}
                notifyTestFinished(scenario, new TestResult(RunStatus.Passed, 1000, "No output"));
            }
        } finally {
            notifySessionFinished();
        }
    }
}
