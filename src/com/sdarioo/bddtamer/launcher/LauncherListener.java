package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;

public interface LauncherListener {

    void scenarioStarted(Scenario scenario);

    void scenarioFinished(Scenario scenario, TestResult result);

    void sessionStarted(List<Scenario> scope);

    void sessionFinished();
}
