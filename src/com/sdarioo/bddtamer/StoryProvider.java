package com.sdarioo.bddtamer;


import com.intellij.openapi.project.Project;
import com.sdarioo.bddtamer.model.Story;

import java.util.List;

public interface StoryProvider {

    List<Story> getStories(Project project);
}
