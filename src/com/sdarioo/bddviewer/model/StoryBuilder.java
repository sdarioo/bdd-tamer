package com.sdarioo.bddviewer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoryBuilder {

    private Location location;

    private List<Scenario> scenarioList = new ArrayList<>();

    public Story build() {
        Objects.nonNull(location);
        return new Story(location, scenarioList);
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
