package com.sdarioo.bddtamer.launcher;

public class TestResult {

    private final RunStatus status;
    private final long time;
    private final String output;

    public TestResult(RunStatus status, long time, String output) {
        this.status = status;
        this.time = time;
        this.output = output;
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
