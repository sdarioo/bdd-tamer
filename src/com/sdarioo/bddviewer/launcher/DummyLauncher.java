package com.sdarioo.bddviewer.launcher;

import com.sdarioo.bddviewer.model.Scenario;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DummyLauncher extends AbstractLauncher {

    @Override
    protected TestResult execute(Scenario scenario) {
        try {
            int time = ThreadLocalRandom.current().nextInt(2000);
            Thread.sleep(time);

            List<String> lines = Files.readAllLines(Paths.get("D:\\Temp\\out.txt"));
            lines.forEach(l -> notifyOutputLine(l));


            return new TestResult(RunStatus.Passed, time, scenario.getName() + " SUCCESS");
        } catch (Exception e) {
            return new TestResult(RunStatus.Failed, 0L, e.toString());
        }
    }
}
