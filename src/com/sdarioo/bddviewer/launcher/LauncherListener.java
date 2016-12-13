package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.util.List;

public interface LauncherListener {

    void scenarioStarted(Scenario scenario);

    void scenarioFinished(Scenario scenario, TestResult result);

    void sessionStarted(List<Scenario> scope, SessionContext context);

    void sessionFinished(SessionContext context);

    void outputLine(String line, Severity severity);

    enum Severity {
        Normal,
        Info,
        Error
    }
}
