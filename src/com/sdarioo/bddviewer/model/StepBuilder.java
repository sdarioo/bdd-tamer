package com.sdarioo.bddviewer.model;

public class StepBuilder {

    private final String text;
    private final Table values = new Table();

    private Location location;

    public StepBuilder(String text) {
        this.text = text;
    }

    public Step build() {
        if (!values.isEmpty() && (location != null)) {
            values.setLocation(new Location(location.getPath(), location.getStartLine() + 1));
        }
        return new Step(text, location, values);
    }

    public String getText() {
        return text;
    }

    public Location getLocation() {
        return location;
    }

    public StepBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public StepBuilder addValues(String line) {
        values.add(line);
        return this;
    }

}
