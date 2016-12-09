package com.sdarioo.bddviewer.launcher.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClasspathBuilder {

    private final Set<String> classpath = new LinkedHashSet<>();


    public static ClasspathBuilder pcmClasspathBuilder(Path projectDir) {
        ClasspathBuilder builder = new ClasspathBuilder();

        List<String> exc = Arrays.asList("cneomi-mapper",
                "uns",
                "jma",
                "jMAObjects",
                "qos-web-objects",
                "jbehave-hpqc-reporter",
                "hpqc-rest-wrapper",
                "pwvault-client");

        Predicate<String> remove = s -> {
            for (String e : exc) {
                if (s.contains(e)) {
                    return false;
                }
            }
            return true;
        };


        try {
            Path cp = Paths.get("c:\\Dev\\pcm_ij\\cm\\app\\cm\\pscm_bdd\\cp.txt");
            String text = new String(Files.readAllBytes(cp));
            String[] paths = text.split(";");
            Arrays.stream(paths).forEach(p -> {
                if (!p.toString().contains("motorolasolutions") || !remove.test(p.toString())) {
                    builder.addEntry(Paths.get(p));
                } else {
                    System.err.println("SKIP: " + p);
                }
            });
        } catch (IOException e) {
        }


        list(projectDir).forEach(path -> {
            if (!path.endsWith("cm-control-panel") && !path.endsWith("cm_service")) {
                builder.addEntry(path.resolve("target\\classes"));
                builder.addEntry(path.resolve("target\\test-classes"));
            }
        });

        return builder;
    }

    public ClasspathBuilder addEntry(Path path) {
        if (Files.exists(path)) {
            System.err.println("ADD ENTRY: " + path);
            classpath.add(path.toString());
        }
        return this;
    }

    public ClasspathBuilder addJars(Path dir) {
        if (Files.isDirectory(dir)) {
            classpath.addAll(list(dir)
                    .filter(ClasspathBuilder::isClasspathJar)
                    .map(Path::toString)
                    .collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public String toString() {
        return classpath.stream().collect(Collectors.joining(";"));
    }

    private static Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            return Stream.of();
        }
    }

    private static boolean isClasspathJar(Path path) {
        return Files.isRegularFile(path)
                && path.toString().endsWith(".jar")
                && !path.toString().endsWith("-sources.jar");
//                && !path.toString().endsWith("-classes.jar")
//                && !path.toString().endsWith("-tests.jar");
    }
}
