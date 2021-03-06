package com.sdarioo.bddviewer.launcher.cmd;

import com.intellij.openapi.project.Project;
import com.sdarioo.bddviewer.ui.console.Console;

import java.awt.*;
import java.util.function.Consumer;

public class ConsoleMock implements Console {

    private final StringBuilder content = new StringBuilder();

    @Override
    public boolean isShowDetails() {
        return false;
    }

    @Override
    public void clear() {
        content.delete(0, content.length());
    }

    @Override
    public int getTextLength() {
        return content.length();
    }

    @Override
    public void print(String text) {
        content.append(text);
    }

    @Override
    public void print(String text, ContentType contentType) {
        content.append(text);
    }

    @Override
    public void print(String text, FontStyle fontStyle, Color color) {
        content.append(text);
    }

    @Override
    public void printHyperlink(String text, Consumer<Project> onClick) {
        content.append(text);
    }

    public String getContent() {
        return content.toString();
    }
}
