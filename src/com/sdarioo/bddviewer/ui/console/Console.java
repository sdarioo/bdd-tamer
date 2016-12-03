package com.sdarioo.bddviewer.ui.console;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;

import java.awt.*;
import java.util.function.Consumer;

public interface Console {

    String LINE_SEPARATOR = "\n";

    void clear();

    int getTextLength();

    void print(String text);

    void print(String text, ContentType contentType);

    void print(String text, FontStyle fontStyle, Color color);

    void printHyperlink(String text, Consumer<Project> onClick);

    default void println() {
        print(LINE_SEPARATOR);
    }

    default void println(String text) {
        print(text + LINE_SEPARATOR);
    }

    default void println(String text, ContentType contentType) {
        print(text + LINE_SEPARATOR, contentType);
    }

    default void println(String text, FontStyle fontStyle, Color color) {
        print(text + LINE_SEPARATOR, fontStyle, color);
    }

    enum ContentType {
        NORMAL(ConsoleViewContentType.NORMAL_OUTPUT_KEY),
        WARNING(ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY),
        ERROR(ConsoleViewContentType.ERROR_OUTPUT_KEY),
        GRAY(ConsoleViewContentType.LOG_EXPIRED_ENTRY);

        TextAttributesKey key;
        ContentType(TextAttributesKey key) {
            this.key = key;
        }
    }

    enum FontStyle {
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
