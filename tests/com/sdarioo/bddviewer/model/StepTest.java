package com.sdarioo.bddviewer.model;

import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;


public class StepTest {

    @Test
    public void testPatternNoVars() {
        Step step = new StepBuilder("And value = 1").build();
        assertEquals("And value = 1", step.getText());
        assertEquals("And value = 1", step.getPattern());
    }

    @Test
    public void testPatternOneVar() {
        Step step = new StepBuilder("And value is <value>").build();
        assertEquals("And value is <value>", step.getText());
        assertEquals("And value is {0}", step.getPattern());
    }

    @Test
    public void testPatternTwoVars() {
        Step step = new StepBuilder("And value is <value> and count is <count>.").build();
        assertEquals("And value is <value> and count is <count>.", step.getText());
        assertEquals("And value is {0} and count is {1}.", step.getPattern());
    }

    @Test
    public void testPatternEmptyVars() throws Exception {
        Step step = new StepBuilder("Step with <value>").build();
        assertEquals("Step with {0}", step.getPattern());

        MessageFormat format = new MessageFormat(step.getPattern());
        format.parse("Step with ");

        step = new StepBuilder("And <value> is not null.").build();
        format = new MessageFormat(step.getPattern());
        format.parse("And  is not null.");
    }
}
