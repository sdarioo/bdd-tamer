package com.sdarioo.bddviewer.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private final List<String> header;
    private final List<List<String>> rows;


    public Table() {
        this.header = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public void add(String line) {
        add(split(line));
    }

    public void add(List<String> data) {
        if (data.isEmpty()) {
            return;
        }
        if (header.isEmpty()) {
            header.addAll(data);
        } else {
            rows.add(new ArrayList<>(data));
        }
    }

    public List<String> getHeader() {
        return Collections.unmodifiableList(header);
    }

    public int getRowsCount() {
        return rows.size();
    }

    public List<String> getRow(int index) {
        return Collections.unmodifiableList(rows.get(index));
    }

    private static List<String> split(String line) {
        return Arrays.stream(line.split("|"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
