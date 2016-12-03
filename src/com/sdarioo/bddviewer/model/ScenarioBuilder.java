package com.sdarioo.bddviewer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScenarioBuilder {

    private String name;
    private Meta meta;
    private Location location;
    private Table examples;
    List<Step> steps = new ArrayList<>();

    public Scenario build() {
        Objects.nonNull(name);
        Objects.nonNull(location);
        return new Scenario(name, meta, location, steps, examples);
    }

    public String getName() {
        return name;
    }

    public ScenarioBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Meta getMeta() {
        return meta;
    }

    public ScenarioBuilder setMeta(Meta meta) {
        this.meta = meta;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public ScenarioBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Table getExamples() {
        return examples;
    }

    public void setExamples(Table examples) {
        this.examples = examples;
    }

    public ScenarioBuilder addStep(Step step) {
        steps.add(step);
        return this;
    }

    public Step lastStep() {
        return steps.isEmpty() ? null : steps.get(steps.size() - 1);
    }
}
