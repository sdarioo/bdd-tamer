package com.sdarioo.bddviewer;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.launcher.CmdLauncher;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.provider.ProjectStoryProvider;
import com.sdarioo.bddviewer.provider.StoryProvider;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Plugin implements ApplicationComponent {

    //private static final Logger LOGGER = LoggerFactory.getLogger(Plugin.class);
    private static final Logger LOGGER = Logger.getInstance(Plugin.class);
    private static final String NAME = "BDD Viewer";

    private static Plugin INSTANCE;

    // Project path -> StoryProvider
    private Map<Path, StoryProvider> storyProviders = new ConcurrentHashMap<>();

    // Project path -> sessionManagers
    private Map<Path, SessionManager> sessionManagers = new ConcurrentHashMap<>();


    private Plugin() {
        INSTANCE = this;
    }

    public static Plugin getInstance() {
        if (INSTANCE == null) {
            LOGGER.error("Plugin not initialized yet!");
        }
        return INSTANCE;
    }

    @Override
    public void initComponent() {
        LOGGER.info("Initialize: " + LocalDateTime.now());
    }

    @Override
    public void disposeComponent() {
        LOGGER.info("Dispose: " + LocalDateTime.now());
    }

    @NotNull
    @Override
    public String getComponentName() {
        return NAME;
    }

    public StoryProvider getStoryProvider(Project project) {
        Path path = Paths.get(project.getProjectFilePath());
        return storyProviders.computeIfAbsent(path, p -> new ProjectStoryProvider());
    }

    public SessionManager getSessionManager(Project project) {
        Path path = Paths.get(project.getProjectFilePath());
        return sessionManagers.computeIfAbsent(path, p -> new SessionManager(new CmdLauncher()));
    }
}
