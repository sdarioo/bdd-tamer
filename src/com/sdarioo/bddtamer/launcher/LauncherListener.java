package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

public interface LauncherListener {

    void scenarioStarted(Scenario scenario);

    void scenarioFinished(Scenario scenario, TestResult result);

    void sessionStarted();

    void sessionFinished();
}
