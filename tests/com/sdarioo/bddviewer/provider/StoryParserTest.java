package com.sdarioo.bddviewer.provider;

import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Story;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StoryParserTest {

    @Test
    public void parseStory() throws IOException {
        Path path = createStoryFile("example1.story");
        try {
            Story story = StoryParser.parse(path);
            assertNotNull(story);
            assertEquals(1, story.getScenarios().size());
            Scenario scenario = story.getScenarios().get(0);
            assertEquals(8, scenario.getSteps().size());
            assertEquals(5, scenario.getExamples().getRowsCount());

            assertTrue(scenario.getSteps().get(1).hasValues());
            assertEquals(2, scenario.getSteps().get(1).getValues().getRowsCount());

        } finally {
            Files.deleteIfExists(path);
        }
    }

    private static Path createStoryFile(String resource) throws IOException {
        try (InputStream is = StoryParserTest.class.getClassLoader().getResourceAsStream(resource)) {
            Path path = Files.createTempFile("test", ".story");
            try (OutputStream out = Files.newOutputStream(path)) {
                int c = 0;
                while ((c = is.read()) >= 0) {
                    out.write(c);
                }
            }
            return path;
        }
    }
}
