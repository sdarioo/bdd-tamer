package com.sdarioo.bddviewer.provider;


import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.model.Story;

import java.util.List;

public interface StoryProvider {

    List<Story> getStories(Project project);

}
