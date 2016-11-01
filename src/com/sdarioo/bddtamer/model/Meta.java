package com.sdarioo.bddtamer.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Meta attributes associated with scenario
 */
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

    public List<String> getRequirementsList() {
        String req = getRequirements();
        return Arrays.stream(req.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
