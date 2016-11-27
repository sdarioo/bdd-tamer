package com.sdarioo.bddviewer.ui.tree;


import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BddTreeColumns {

    public static String NAME_COLUMN = "Name";
    public static String L2_COLUMN = "L2";
    public static String TIME_COLUMN = "Exec. Time";

    private final SessionManager sessionManager;
    private final List<ColumnInfo> columnInfos;

    public BddTreeColumns(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.columnInfos = createColumns();
    }

    public List<String> getColumnNames() {
        return getColumns().stream().map(ColumnInfo::getName).collect(Collectors.toList());
    }

    public List<Object> getColumnValues(Object userObject) {
        return getColumns().stream().map(c -> c.getValue(userObject)).collect(Collectors.toList());
    }

    public List<ColumnInfo> getColumns() {
        return columnInfos;
    }

    private List<ColumnInfo> createColumns() {
        List<ColumnInfo> columns = new ArrayList<>();
        columns.add(new ColumnInfo(NAME_COLUMN, 1000) {
            @Override
            Object getValue(Object userObject) {
                return userObject;
            }
        });
        columns.add(new ColumnInfo(L2_COLUMN, 400) {
            @Override
            Object getValue(Object userObject) {
                return (userObject instanceof Scenario) ? ((Scenario)userObject).getMeta().getRequirements() : "";
            }
        });
        columns.add(new ColumnInfo(TIME_COLUMN, 150) {
            @Override
            Object getValue(Object userObject) {
                if (userObject instanceof Scenario) {
                    Scenario scenario = (Scenario)userObject;
                    TestResult result = sessionManager.getResult(scenario);
                    if (result != null) {
                        return String.valueOf(result.getTime()) + "ms";
                    }
                }
                return "";
            }
        });
        return columns;
    }

    public static abstract class ColumnInfo {
        private final String name;
        private final int preferredWidth;

        ColumnInfo(String name, int preferredWidth) {
            this.name = name;
            this.preferredWidth = preferredWidth;
        }

        abstract Object getValue(Object userObject);

        public String getName() {
            return name;
        }

        public int getPreferredWidth() {
            return preferredWidth;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
