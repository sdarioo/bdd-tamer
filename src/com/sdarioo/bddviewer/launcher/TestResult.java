package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

public class TestResult {

    private final RunStatus status;
    private final long time;
    private final String output;

    public TestResult(RunStatus status, long time, String output) {
        this.status = status;
        this.time = time;
        this.output = output;
    }

    public static TestResult skipped(Scenario scenario) {
        return new TestResult(RunStatus.Skipped, 0L, "Scenario skipped: " + scenario.getName());
    }

    public RunStatus getStatus() {
        return status;
    }

    public long getTime() {
        return time;
    }

    public String getOutput() {
        return output;
    }

}
