package com.sdarioo.bddviewer.model;

import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;


public class StepTest {

    @Test
    public void testPatternNoVars() {
        Step step = new Step("And value = 1");
        assertEquals("And value = 1", step.getText());
        assertEquals("And value = 1", step.getPattern());
    }

    @Test
    public void testPatternOneVar() {
        Step step = new Step("And value is <value>");
        assertEquals("And value is <value>", step.getText());
        assertEquals("And value is {0}", step.getPattern());
    }

    @Test
    public void testPatternTwoVars() {
        Step step = new Step("And value is <value> and count is <count>.");
        assertEquals("And value is <value> and count is <count>.", step.getText());
        assertEquals("And value is {0} and count is {1}.", step.getPattern());
    }

    @Test
    public void testPatternEmptyVars() throws Exception {
        Step step = new Step("Step with <value>");
        assertEquals("Step with {0}", step.getPattern());

        MessageFormat format = new MessageFormat(step.getPattern());
        format.parse("Step with ");

        step = new Step("And <value> is not null.");
        format = new MessageFormat(step.getPattern());
        format.parse("And  is not null.");
    }
}
