package com.sdarioo.bddviewer.launcher;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {
    private final Map<String, Object> properties = new HashMap<>();

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }
}
