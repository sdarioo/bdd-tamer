package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.util.List;

public interface Launcher {

    void submit(List<Scenario> scenarios) throws LauncherException;

    void addListener(LauncherListener listener);

    void removeListener(LauncherListener listener);

}
