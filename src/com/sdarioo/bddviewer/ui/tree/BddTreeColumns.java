package com.sdarioo.bddviewer.ui.tree;


import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;
import com.sdarioo.bddviewer.model.Story;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
                        return formatTime(result.getTime());
                    }
                } else if (userObject instanceof Story) {
                    List<Scenario> scenarios = ((Story) userObject).getScenarios();
                    AtomicLong total = new AtomicLong();
                    scenarios.forEach(s -> {
                        TestResult result = sessionManager.getResult(s);
                        if (result != null) {
                            total.addAndGet(result.getTime());
                        }
                    });
                    if (total.get() > 0) {
                        return formatTime(total.get());
                    }
                }
                return "";
            }
        });
        return columns;
    }

    private static String formatTime(long millis) {
        StringBuilder sb = new StringBuilder();

        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis = millis - min * 60000;
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis = millis - sec * 1000;
        if (min > 0) {
            sb.append(min + "m ");
            sb.append(sec + "s ");
        } else if (sec > 0) {
            sb.append(sec + "s ");
        }
        sb.append(millis + "ms");
        return sb.toString();
    }

    private static String padLeft(String text, int n) {
        n = Math.max(n, 0);
        return String.format("%1$" + n + "s", text);
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
