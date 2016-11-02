package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.util.concurrent.ThreadLocalRandom;

public class DummyLauncher extends AbstractLauncher {

    @Override
    protected TestResult execute(Scenario scenario) {
        try {
            int time = ThreadLocalRandom.current().nextInt(2000);
            Thread.sleep(time);
            return new TestResult(RunStatus.Passed, time, scenario.getName() + " SUCCESS");
        } catch (InterruptedException e) {
            return new TestResult(RunStatus.Failed, 0L, e.toString());
        }
    }
}
