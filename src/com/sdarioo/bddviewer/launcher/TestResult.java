package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

public class TestResult {

    private final RunStatus status;
    private final long time;


    public TestResult(RunStatus status, long time) {
        this.status = status;
        this.time = time;
    }

    public static TestResult skipped(Scenario scenario) {
        return new TestResult(RunStatus.Skipped, 0L);
    }

    public RunStatus getStatus() {
        return status;
    }

    public long getTime() {
        return time;
    }

}
