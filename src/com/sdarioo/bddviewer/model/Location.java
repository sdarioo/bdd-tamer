package com.sdarioo.bddviewer.model;


import java.nio.file.Path;

public class Location {

    private final Path path;

    /** First line starting from 1 */
    private final int startLine;

    /** Last line (inclusive) starting from 1 */
    private final int endLine;

    public Location(Path path, int startLine) {
        this(path, startLine, startLine);
    }

    public Location(Path path, int startLine, int endLine) {
        this.path = path;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public Path getPath() {
        return path;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }
}
