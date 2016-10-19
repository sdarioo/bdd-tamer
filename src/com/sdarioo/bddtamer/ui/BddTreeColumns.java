package com.sdarioo.bddtamer.ui;


import com.sdarioo.bddtamer.model.Scenario;

import java.util.function.Function;

public enum BddTreeColumns {

    NAME("Name", modelObject -> modelObject),
    REQUIREMENT("Requirement", BddTreeColumns::getRequirement);

    private final String name;
    private final Function<Object, Object> valueProvider;

    BddTreeColumns(String name, Function<Object, Object> valueProvider) {
        this.name = name;
        this.valueProvider = valueProvider;
    }

    public String getName() {
        return name;
    }

    public Object getValue(Object modelObject) {
        return valueProvider.apply(modelObject);
    }

    private static Object getRequirement(Object modelObject) {
        return (modelObject instanceof Scenario) ? ((Scenario)modelObject).getMeta().getRequirements() : "";
    }
}
