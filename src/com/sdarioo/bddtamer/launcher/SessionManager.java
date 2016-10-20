package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

public class SessionManager implements LauncherListener {

    private static final SessionManager INSTANCE = new SessionManager();

    private boolean isRunning;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public void scenarioStarted(Scenario scenario) {
        isRunning = true;
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {

    }

    @Override
    public void sessionStarted() {

    }

    @Override
    public void sessionFinished() {

    }
}
