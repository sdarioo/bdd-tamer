package com.sdarioo.bddviewer.model;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StoryTest {

    @Test
    public void testStoryName() {
        Story story = newStory("C:/temp/funny_story.story");
        assertEquals("FunnyStory", story.getName());
    }

    @Test
    public void testJavaPath() {
        Story story = newStory("C:/temp/funny_story.story");
        assertEquals("C:\\temp\\FunnyStory.java", story.getJavaPath().toString());
    }

    @Test
    public void testIsRunnable() {
        Story story = newStory("C:/temp/funny_story.story");
        assertFalse(story.isRunnable());
    }

    private static Story newStory(String storyName) {
        Location loc = new Location(Paths.get(storyName), 1, 1);
        return new Story(loc, Collections.emptyList());
    }
}
