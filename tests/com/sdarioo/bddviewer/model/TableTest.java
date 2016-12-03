package com.sdarioo.bddviewer.model;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TableTest {

    @Test
    public void testSplit() throws Exception {
        String row = "|a|b|c|d|e|f|g|h|i|j|k|";
        List<String> values = Table.split(row);
        assertEquals(11, values.size());
        assertEquals("a", values.get(0));
        assertEquals("k", values.get(10));
    }

    @Test
    public void testFormatting() throws Exception {
        String[] values = {"a", "b", "c"};
        String line = Table.format(values);
        assertEquals("| a | b | c |", line);
        assertArrayEquals(values, Table.split(line).toArray(new String[0]));
    }

    @Test
    public void splitEmptyValues() throws Exception {
        String row = "||";
        List<String> values = Table.split(row);
        assertEquals(1, values.size());

        row = " | | ";
        values = Table.split(row);
        assertEquals(1, values.size());

        row = "|||";
        values = Table.split(row);
        assertEquals(2, values.size());
    }
}
