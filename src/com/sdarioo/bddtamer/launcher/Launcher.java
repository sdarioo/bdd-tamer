package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.util.List;

public interface Launcher {

    void submit(List<Scenario> scenarios) throws LauncherException;

    void addListener(LauncherListener listener);

    void removeListener(LauncherListener listener);

}
