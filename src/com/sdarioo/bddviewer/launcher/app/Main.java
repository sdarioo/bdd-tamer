package com.sdarioo.bddviewer.launcher.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static final String STORY_ARG = "--story=";
    public static final String REPORT_ARG = "--report=";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Invalid number of program arguments.");
            System.exit(1);
        }

        Path moduleDir = Paths.get(args[0]);
        if (!Files.isDirectory(moduleDir)) {
            System.err.println("BDD project directory doesn't exists: " + moduleDir);
            System.exit(1);
        }

        ScenarioRunner runner = new ScenarioRunner(moduleDir);
        Path reportDir = getReportDir(args);
        List<Path> stories = getSorties(args);
        for (Path story : stories) {
            try {
                runner.run(story, reportDir);
            } catch (Throwable thr) {
                System.err.println("Exception while executing story: " + story.getFileName());
                System.err.println(thr);
            }
        }
        System.exit(0);
    }

    private static List<Path> getSorties(String[] args) {
        List<Path> result = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith(STORY_ARG)) {
                result.add(Paths.get(arg.substring(STORY_ARG.length())));
            }
        }
        return result;
    }

    private static Path getReportDir(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(REPORT_ARG)) {
                return Paths.get(arg.substring(REPORT_ARG.length()));
            }
        }
        return null;
    }

}
