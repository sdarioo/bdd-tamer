package com.sdarioo.bddviewer.model;

public class Step implements LocationHolder {

    private final String text;
    private final Table values;
    private final Location location;

    Step(String text, Location location, Table values) {
        this.text = text;
        this.values = values;
        this.location = location;
    }

    public boolean hasValues() {
        return values.getRowsCount() > 0;
    }

    public Table getValues() {
        return values;
    }

    public String getText() {
        return text;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * If step contains variables then return step text with variable placeholders e.g
     * Step: And setting value for <field> to: <value>
     * Pattern: And setting value for {0} to: {1}
     * @return step text with variables placeholders
     */
    public String getPattern() {
        String result = text;
        int count = 0;
        int lIndex = result.indexOf('<');

        while (lIndex > 0) {
            int rIndex = result.indexOf('>', lIndex);
            if (rIndex > lIndex) {
                result = result.substring(0, lIndex) + '{' + count + '}' + result.substring(rIndex + 1);
                count++;
            }
            lIndex = result.indexOf('<', lIndex + 1);
        }
        return result;
    }

    @Override
    public String toString() {
        return text;
    }
}
