package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;

public class LauncherListenerAdapter implements LauncherListener {

    @Override
    public void scenarioStarted(Scenario scenario) {
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
    }

    @Override
    public void sessionStarted(List<Scenario> scope) {
    }

    @Override
    public void sessionFinished() {
    }
}
