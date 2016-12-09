package com.sdarioo.bddviewer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipUtil {

    private ZipUtil() {}

    public static List<String> list(Path jarFile) throws IOException {
        List<String> result = new ArrayList<>();
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.toFile()))) {
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    result.add(className.substring(0, className.length() - ".class".length()));
                }
            }
        }
        return result;
    }

}
