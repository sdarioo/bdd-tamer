package com.sdarioo.bddtamer.launcher;

import com.sdarioo.bddtamer.model.Scenario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CmdLauncher extends AbstractLauncher {

    @Override
    public void submit(List<Scenario> scenarios) throws LauncherException {

    }


    private final Path writeToTempFile(Scenario scenario) throws IOException {
        Path path = File.createTempFile("bdd_scenario", ".txt").toPath();
        // TODO
        return path;
    }
}
