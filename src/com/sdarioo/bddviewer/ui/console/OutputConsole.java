package com.sdarioo.bddviewer.ui.console;

import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class OutputConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputConsole.class);

    private final JTextPane textPane;
    private final JScrollPane scrollPane;


    public OutputConsole(SessionManager sessionManager) {
        textPane = new JTextPane(new DefaultStyledDocument(new StyleContext()));
        textPane.setEditable(false);

        scrollPane = new JBScrollPane(textPane);

        addLaunchListener(sessionManager.getLauncher());
    }

    public JComponent getComponent()  {
        return scrollPane;
    }

    public void append(String text) {
        SwingUtilities.invokeLater(() -> {
            appendText(text, Color.BLUE);
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() ->  textPane.setText(null));
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
                String[] lines = message.split("\\n");
                Arrays.stream(lines).forEach(line -> append(line + '\n'));
            }
        });
    }

    private static boolean isLoggerLine(String line) {
        return line.contains("WARN") || line.contains("ERROR") || line.contains("DEBUG") || line.contains("INFO");
    }

//    private Color getLineColor(String line) {
//        if (line.contains("ERROR")) {
//            return Color.red;
//        }
//        if (line.contains("WARN")) {
//            return Color.pink;
//        }
//        if (line.contains("INFO")) {
//
//        }
//    }


    private void appendText(String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        attrSet = sc.addAttribute(attrSet, StyleConstants.FontFamily, "Lucida Console");
        attrSet = sc.addAttribute(attrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = textPane.getDocument().getLength();
        try {
            textPane.getDocument().insertString(len, msg, attrSet);
        } catch (BadLocationException e) {
            LOGGER.error("Error appending text.", e);
        }
        //textPane.setCaretPosition(len);
        //textPane.setCharacterAttributes(attrSet, false);

    }

}
