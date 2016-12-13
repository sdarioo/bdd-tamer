package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.util.List;

public class LauncherListenerAdapter implements LauncherListener {

    @Override
    public void scenarioStarted(Scenario scenario) {
    }

    @Override
    public void scenarioFinished(Scenario scenario, TestResult result) {
    }

    @Override
    public void sessionStarted(List<Scenario> scope, SessionContext context) {
    }

    @Override
    public void sessionFinished(SessionContext context) {
    }

    @Override
    public void outputLine(String line, Severity severity) {
    }
}
