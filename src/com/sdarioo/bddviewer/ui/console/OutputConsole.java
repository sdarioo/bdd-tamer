package com.sdarioo.bddviewer.ui.console;

import com.intellij.execution.impl.ConsoleViewUtil;
import com.intellij.execution.impl.EditorHyperlinkSupport;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.ui.components.JBScrollPane;
import com.sdarioo.bddviewer.launcher.Launcher;
import com.sdarioo.bddviewer.launcher.LauncherListenerAdapter;
import com.sdarioo.bddviewer.launcher.SessionManager;
import com.sdarioo.bddviewer.launcher.TestResult;
import com.sdarioo.bddviewer.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;


public class OutputConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputConsole.class);
    private static final String LINE_SEPARATOR = "\n";

    private final EditorEx editor;
    private final EditorHyperlinkSupport hyperlinkSupport;
    private final JScrollPane scrollPane;

    private boolean showLogs = false;


    public OutputConsole(Project project, SessionManager sessionManager) {

        editor = ConsoleViewUtil.setupConsoleEditor(project, false, false);
        hyperlinkSupport = new EditorHyperlinkSupport(editor, project);

        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerAdapter() {
            public void projectClosed(Project project) {
            EditorFactory.getInstance().releaseEditor(editor);
            }
        });

        scrollPane = new JBScrollPane(editor.getComponent());
        addLaunchListener(sessionManager.getLauncher());
    }

    public JComponent getComponent()  {
        return scrollPane;
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean value) {
        showLogs = value;
    }

    public void appendText(String text) {
        SwingUtilities.invokeLater(() -> {
            internalAppend(text);
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() ->  {
            editor.getDocument().deleteString(0, editor.getDocument().getTextLength());
        });
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
                    appendText(text);
                    appendText(LINE_SEPARATOR);
                }
            }
            @Override
            public void output(String message) {
                appendText(message);
            }
        });
    }

    private static boolean isLoggerLine(String line) {
        return line.contains("WARN") || line.contains("ERROR") || line.contains("DEBUG") || line.contains("INFO");
    }

    private void internalAppend(String text) {
        text = fixLineSeparators(text);
        DocumentEx document = editor.getDocument();
        int startOffset = document.getTextLength();
        document.insertString(startOffset, text);

        // ConsoleViewContentType.NORMAL_OUTPUT_KEY
        // ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY
        TextAttributesKey key = ConsoleViewContentType.NORMAL_OUTPUT_KEY;
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);

        editor.getMarkupModel().addRangeHighlighter(startOffset, document.getTextLength(), 2001, attributes, HighlighterTargetArea.EXACT_RANGE);


        startOffset = document.getTextLength();
        document.insertString(startOffset, "GoTo");
        hyperlinkSupport.createHyperlink(startOffset, document.getTextLength(), (TextAttributes)null, project -> {

        });
    }

    private static String fixLineSeparators(String text) {
        return text.replace("\r\n", "\n");
    }

}
