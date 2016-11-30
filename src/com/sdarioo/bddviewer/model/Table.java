package com.sdarioo.bddviewer.model;


import com.intellij.openapi.diagnostic.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

    private static final Logger LOGGER = Logger.getInstance(Table.class);

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

    public List<String> getLastRow() {
        return Collections.unmodifiableList(rows.get(rows.size() - 1));
    }

    public static List<String> split(String line) {
        line = line.trim();
        if (line.startsWith("|")) {
            line = line.substring(1);
        }
        if (line.endsWith("|")) {
            line = line.substring(0, line.length() - 1);
        }
        return Arrays.stream(line.split("\\|", -1))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
