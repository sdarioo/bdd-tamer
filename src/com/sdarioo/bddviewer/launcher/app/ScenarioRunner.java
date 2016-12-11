package com.sdarioo.bddviewer.launcher.app;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.io.StoryLocation;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterControls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ScenarioRunner {

    private final long timeoutInSecs = 600;
    private final Path moduleDir;

    public ScenarioRunner(Path moduleDir) {
        this.moduleDir = moduleDir;
    }

    public void run(Path storyFile, Path reportsDir) {
        Embedder executor = new Embedder();
        Configuration configuration = configuration(reportsDir);
        executor.useConfiguration(configuration);
        executor.useCandidateSteps(getCandidateSteps(configuration, storyFile));
        executor.embedderControls().useStoryTimeoutInSecs(timeoutInSecs);
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add(storyFile.toString());
        executor.runStoriesAsPaths(fileList);
    }

    private Configuration configuration(Path reportsDir) {
        Format[] formats = new Format[] { Format.CONSOLE };
        StoryReporterBuilder builder = new StoryReporterBuilder().withDefaultFormats().withFormats(formats);
        ParameterControls pc = new ParameterControls().useDelimiterNamedParameters(true);
        Configuration result = new MostUsefulConfiguration()
                .useStoryReporterBuilder(builder)
                .useStoryLoader(new CustomStoryLoader())
                .useParameterControls(pc);

        result.storyReporterBuilder().withPathResolver(new CustomFilePathResolver(reportsDir));

        return result;
    }

    private List<CandidateSteps> getCandidateSteps(Configuration configuration, Path storyFile) {
        Object[] steps = CandidateStepsFinder.getCandidateSteps(moduleDir, storyFile);
        return new InstanceStepsFactory(configuration, steps).createCandidateSteps();
    }

    private static class CustomStoryLoader implements StoryLoader {
        @Override
        public String loadStoryAsText(String storyPath) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(storyPath));
                return new String(bytes);
            } catch (IOException exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
    }

    private static class CustomFilePathResolver implements FilePrintStreamFactory.FilePathResolver {
        private final Path staticDir;

        CustomFilePathResolver(Path staticDir) {
            this.staticDir = staticDir;
        }

        @Override
        public String resolveDirectory(StoryLocation storyLocation, String relativeDirectory) {
            String result = staticDir.toFile().getPath();
            return result;
        }

        @Override
        public String resolveName(StoryLocation storyLocation, String extension) {
            String result = new File(storyLocation.getPath()).getName() + "." + extension;
            return result;
        }

    }
}
