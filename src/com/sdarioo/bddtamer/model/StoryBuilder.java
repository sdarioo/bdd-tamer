package com.sdarioo.bddtamer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoryBuilder {

    private String name;
    private Location location;

    private List<Scenario> scenarioList = new ArrayList<>();

    public Story build() {
        Objects.nonNull(name);
        Objects.nonNull(location);
        return new Story(name, location, scenarioList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addScenario(Scenario scenario) {
        scenarioList.add(scenario);
    }
}
