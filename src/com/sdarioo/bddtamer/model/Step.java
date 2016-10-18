package com.sdarioo.bddtamer.model;


public class Step {

    private final String text;

    private Table table = new Table();

    public Step(String text) {
        this.text = text;
    }

    public Table getTable() {
        return table;
    }
}
