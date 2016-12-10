package com.sdarioo.bddviewer.launcher.app;

import com.sdarioo.bddviewer.util.ClasspathDirectory;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;


final class CandidateStepsFinder {

    private CandidateStepsFinder() {}

    public static Object[] getCandidateSteps(Path moduleDir, Path storyFile) {

        ClasspathDirectory bddClasses = new ClasspathDirectory(moduleDir.resolve("target/test-classes"));

        String storyName = storyFile.getFileName().toString();
        String storyClassName = toJavaClassName(storyName);

        Class<?> storyClass = bddClasses.loadClassWithName(storyClassName);
        Class<?> abstractStoryClass = bddClasses.loadClassWithName("AbstractBoxTest");

        try {
            Object instance = storyClass.newInstance();
            Method method = abstractStoryClass.getDeclaredMethod("getStepsObjects");
            method.setAccessible(true);
            return (Object[])method.invoke(instance);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Failed to create candidate steps", e);
        }
    }

    private static String toJavaClassName(String storyName) {
        storyName = getNameWithoutExtension(storyName);
        return Arrays.stream(storyName.split("_"))
                .map(CandidateStepsFinder::capitalize)
                .collect(Collectors.joining());
    }

    private static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static String getNameWithoutExtension(String name) {
        int index = name.lastIndexOf('.');
        return (index >= 0) ? name.substring(0, index) : name;
    }
}
