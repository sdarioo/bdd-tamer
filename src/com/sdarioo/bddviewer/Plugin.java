package com.sdarioo.bddviewer;

import com.intellij.openapi.components.ApplicationComponent;
import com.sdarioo.bddviewer.launcher.CmdLauncher;
import com.sdarioo.bddviewer.launcher.DummyLauncher;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.provider.ProjectStoryProvider;
import com.sdarioo.bddviewer.provider.StoryProvider;
import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;


import java.time.LocalDateTime;


public class Plugin implements ApplicationComponent {

    //private static final Logger LOGGER = LoggerFactory.getLogger(Plugin.class);
    private static final Logger LOGGER = Logger.getInstance(Plugin.class);
    private static final String NAME = "BDD Viewer";

    private static Plugin INSTANCE;

    private StoryProvider storyProvider;
    private Launcher launcher;
    private SessionManager sessionManager;

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

        launcher = new DummyLauncher();
        //launcher = new CmdLauncher();
        storyProvider = new ProjectStoryProvider();
        sessionManager = new SessionManager(launcher);
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

    public Launcher getLauncher() {
        return launcher;
    }

    public StoryProvider getStoryProvider() {
        return storyProvider;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
