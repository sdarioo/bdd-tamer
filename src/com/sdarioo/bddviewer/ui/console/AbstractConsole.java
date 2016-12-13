package com.sdarioo.bddviewer.ui.console;

import com.intellij.execution.impl.ConsoleViewUtil;
import com.intellij.execution.impl.EditorHyperlinkSupport;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;


public class AbstractConsole implements Console {
    protected static final Logger LOGGER = Logger.getInstance(AbstractConsole.class);


    protected final EditorEx editor;
    private final EditorHyperlinkSupport hyperlinkSupport;

    public AbstractConsole(Project project) {
        editor = ConsoleViewUtil.setupConsoleEditor(project, false, false);
        hyperlinkSupport = new EditorHyperlinkSupport(editor, project);

        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerAdapter() {
            public void projectClosed(Project closedProject) {
            if (closedProject.getName().equals(project.getName())) {
                EditorFactory.getInstance().releaseEditor(editor);
            }
            }
        });
    }

    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void print(String text) {
        print(text, null, null);
    }

    @Override
    public void print(String text, ContentType contentType) {
        print(text, contentType, null, null);
    }

    @Override
    public void print(String text, FontStyle fontStyle, Color color) {
        print(text, null, fontStyle, color);
    }

    @Override
    public void printHyperlink(String text, Consumer<Project> onClick) {
        UIUtil.invokeLaterIfNeeded(() -> {
            DocumentEx document = editor.getDocument();
            int offset = document.getTextLength();
            document.insertString(offset, fixLineSeparators(text));
            hyperlinkSupport.createHyperlink(offset, document.getTextLength(), null, project -> {
                onClick.accept(project);
            });
        });
    }

    @Override
    public void clear() {
        UIUtil.invokeLaterIfNeeded(() ->  {
            editor.getDocument().deleteString(0, editor.getDocument().getTextLength());
        });
    }

    @Override
    public int getTextLength() {
        return editor.getDocument().getTextLength();
    }

    private void print(String text, ContentType contentType, FontStyle fontStyle, Color color) {
        if (text.length() == 0) {
            return;
        }
        UIUtil.invokeLaterIfNeeded(() -> {
            DocumentEx document = editor.getDocument();
            int offset = document.getTextLength();
            document.insertString(offset, fixLineSeparators(text));

            if (contentType != null) {
                highlight(offset, document.getTextLength(), contentType.key);
            }
            if ((fontStyle != null) || (color != null)) {
                highlight(offset, document.getTextLength(), fontStyle, color);
            }
            scrollToEnd();
        });
    }

    private RangeHighlighter highlight(int startOffset, int endOffset, FontStyle style, Color color) {
        TextAttributes bold = new TextAttributes(color, null, null, null, (style != null ? style.value : 0));
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
        JScrollBar scrollBar = editor.getScrollPane().getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    private static String fixLineSeparators(String text) {
        return text.replace("\r\n", "\n");
    }

}
