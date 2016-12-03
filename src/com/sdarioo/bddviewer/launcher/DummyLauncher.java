package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DummyLauncher extends AbstractLauncher {


    @Override
    protected void executeAll(List<Scenario> scenario) {
        scenario.forEach(this::execute);
    }

    private void execute(Scenario scenario) {
        notifyTestStarted(scenario);

        TestResult result;
        if (scenario.isRunnable()) {
            try {
                int time = ThreadLocalRandom.current().nextInt(2000);
                Thread.sleep(time);

                List<String> lines = Files.readAllLines(Paths.get("D:\\Temp\\out2.txt"));
                lines.forEach(l -> notifyOutputLine(l));

                result = new TestResult(RunStatus.Passed, time);
            } catch (Exception e) {
                result = new TestResult(RunStatus.Failed, 0L);
            }
        } else {
            result = TestResult.skipped(scenario);
        }
        notifyTestFinished(scenario, result);
    }

    @Override
    public void terminate() {
    }
}
