package com.sdarioo.bddviewer.ui.console;

import com.intellij.execution.impl.ConsoleViewUtil;
import com.intellij.execution.impl.EditorHyperlinkSupport;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.ui.components.JBScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.function.Consumer;


public class AbstractConsole {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsole.class);
    protected static final String LINE_SEPARATOR = "\n";

    private final EditorEx editor;
    private final EditorHyperlinkSupport hyperlinkSupport;
    private final JScrollPane scrollPane;

    public AbstractConsole(Project project) {
        editor = ConsoleViewUtil.setupConsoleEditor(project, false, false);
        hyperlinkSupport = new EditorHyperlinkSupport(editor, project);
        scrollPane = new JBScrollPane(editor.getComponent());

        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerAdapter() {
            public void projectClosed(Project closedProject) {
                if (closedProject.getName().equals(project.getName())) {
                    EditorFactory.getInstance().releaseEditor(editor);
                }
            }
        });
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    public void appendText(String text) {
        appendText(text, null, null);
    }

    public void appendText(String text, ContentType contentType) {
        appendText(text, contentType, null);
    }

    public void appendText(String text, FontStyle fontStyle) {
        appendText(text, null, fontStyle);
    }

    public void appendText(String text, ContentType contentType, FontStyle fontStyle) {
        SwingUtilities.invokeLater(() -> {

            DocumentEx document = editor.getDocument();
            int offset = document.getTextLength();
            document.insertString(offset, fixLineSeparators(text));

            if (contentType != null) {
                highlight(offset, document.getTextLength(), contentType.key);
            }
            if (fontStyle != null) {
                highlight(offset, document.getTextLength(), fontStyle);
            }
            scrollToEnd();
        });
    }

    public void appendHyperlink(String text, Consumer<Project> onClick) {
        SwingUtilities.invokeLater(() -> {
            DocumentEx document = editor.getDocument();
            int offset = document.getTextLength();
            document.insertString(offset, fixLineSeparators(text));
            hyperlinkSupport.createHyperlink(offset, document.getTextLength(), null, project -> {
                onClick.accept(project);
            });
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() ->  {
            editor.getDocument().deleteString(0, editor.getDocument().getTextLength());
        });
    }

    private RangeHighlighter highlight(int startOffset, int endOffset, FontStyle style) {
        TextAttributes bold = new TextAttributes(null, null, null, null, style.value);
        return highlight(startOffset, endOffset, bold);
    }

    private RangeHighlighter highlight(int startOffset, int endOffset, TextAttributesKey key) {
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);
        return highlight(startOffset, endOffset, attributes);
    }

    private RangeHighlighter highlight(int startOffset, int endOffset, TextAttributes attributes) {
        MarkupModel markupModel = editor.getMarkupModel();
        int layer = 2001;
        return markupModel.addRangeHighlighter(startOffset, endOffset, layer, attributes, HighlighterTargetArea.EXACT_RANGE);
    }

    private void scrollToEnd() {
        JScrollBar sb = scrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
    }

    private static String fixLineSeparators(String text) {
        return text.replace("\r\n", "\n");
    }

    public enum ContentType {
        NORMAL(ConsoleViewContentType.NORMAL_OUTPUT_KEY),
        WARNING(ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY),
        ERROR(ConsoleViewContentType.ERROR_OUTPUT_KEY);

        TextAttributesKey key;
        ContentType(TextAttributesKey key) {
            this.key = key;
        }
    }

    public enum FontStyle {
        PLAIN(0),
        BOLD(1),
        ITALIC(2),
        BOLD_ITALIC(3);

        int value;
        FontStyle(int value) {
            this.value = value;
        }
    }
}
