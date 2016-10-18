package com.sdarioo.bddtamer.model;

import java.util.HashMap;
import java.util.Map;

public class Meta {
    private final Map<String, String> props = new HashMap<>();

    private static final String REQUIREMENT_KEY = "Requirement";

    public void add(String key, String value) {
        props.put(key, value);
    }

    public String get(String key) {
        return props.get(key);
    }

    public String getRequirements() {
        return get(REQUIREMENT_KEY);
    }
}
