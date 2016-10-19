package com.sdarioo.bddtamer.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Story implements LocationHolder {

    private final String name;
    private final Location location;
    private final List<Scenario> scenarios;

    public Story(String name, Location location, List<Scenario> scenarios) {
        this.scenarios = new ArrayList<>(scenarios);
        this.name = name;
        this.location = location;
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
}
