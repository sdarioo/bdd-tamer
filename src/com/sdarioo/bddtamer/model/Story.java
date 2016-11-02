package com.sdarioo.bddtamer.model;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents .story file consisting of one or more scenarios.
 */
public class Story implements LocationHolder {

    private final String name;
    private final Location location;
    private final List<Scenario> scenarios;

    public Story(Location location, List<Scenario> scenarios) {
        this.location = location;
        this.name = getNameWithoutExtension(getJavaPath());
        this.scenarios = new ArrayList<>(scenarios);

        // Set parent story for child scenarios
        this.scenarios.forEach(s -> s.setStory(this));
    }

    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public List<Scenario> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return whether this story can be automatically executed
     */
    public boolean isRunnable() {
        return Files.isRegularFile(getJavaPath());
    }

    /**
     * @return story java file path. Note that java file path may not exists (e.g for manual stories)
     */
    public Path getJavaPath() {
        Path path = getLocation().getPath();
        String name = getNameWithoutExtension(path);

        String[] parts = name.split("_");
        String camelCaseName = Arrays.stream(parts)
                .map(Story::capitalize)
                .collect(Collectors.joining());

        return path.resolveSibling(camelCaseName + ".java");
    }

    private static String getNameWithoutExtension(Path path) {
        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index > 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    private static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
