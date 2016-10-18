package com.sdarioo.bddtamer.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scenario {

    private final String name;
    private final Meta meta;
    private final Location location;
    private final List<Step> steps;
    private final Table examples;

    Scenario(String name, Meta meta, Location location, List<Step> steps, Table examples) {
        this.name = name;
        this.meta = meta;
        this.location = location;
        this.steps = new ArrayList<>(steps);
        this.examples = examples;
    }

    public String getName() {
        return name;
    }

    public Meta getMeta() {
        return meta;
    }

    public Location getLocation() {
        return location;
    }

    public Table getExamples() {
        return examples;
    }

    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }


}
