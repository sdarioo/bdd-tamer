package com.sdarioo.bddviewer.ui.console;

import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;

import javax.swing.*;
import java.util.List;


public class OutputConsole {

    private final JTextArea textArea;
    private final JScrollPane scrollPane;


    public OutputConsole(SessionManager sessionManager) {
        textArea = new JTextArea();
        textArea.setEditable(false);

        scrollPane = new JBScrollPane(textArea);

        addLaunchListener(sessionManager.getLauncher());
    }

    public JComponent getComponent()  {
        return scrollPane;
    }

    public void append(String text) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(text);
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() ->  textArea.setText(null));
    }

    private void addLaunchListener(Launcher launcher) {
        launcher.addListener(new LauncherListenerAdapter() {
            @Override
            public void sessionStarted(List<Scenario> scope) {
                clear();
            }
            @Override
            public void scenarioFinished(Scenario scenario, TestResult result) {
                String text = result.getOutput();
                if (text != null) {
                    append(System.getProperty("line.separator"));
                    append(text);
                }
            }
            @Override
            public void output(String message) {
                append(message);
            }
        });
    }

}
