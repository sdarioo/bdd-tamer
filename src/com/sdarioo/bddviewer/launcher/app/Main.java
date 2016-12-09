package com.sdarioo.bddviewer.launcher.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class Main {

    public static void main(String[] args) throws Exception {

        Path rootDir = Paths.get("C:\\Dev\\pcm_ij\\cm\\app\\cm");
//        ScenarioRunner runner = new ScenarioRunner(rootDir);
//        runner.run(
//                Paths.get("c:\\Temp\\device_box_test.story"),
//                Paths.get("c:\\Temp\\device_box_test_story"));
//
//        System.exit(0);


//        String text = new String(Files.readAllBytes(Paths.get("c:\\Dev\\pcm_ij\\cm\\app\\cm\\pscm_bdd\\cp.txt")));
//        String[] cp = text.split(";");
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("SET CLASSPATH=c:\\Dev\\pcm_ij\\cm\\app\\cm\\pscm_bdd\\target\\test-classes");
//        sb.append(LS);
//        sb.append("SET CLASSPATH=%CLASSPATH%;C:\\Temp\\lib\\bddviewer.jar");
//        sb.append(LS);
//
//        Arrays.stream(cp).forEach(s -> {
//            sb.append("SET CLASSPATH=%CLASSPATH%;" + s);
//            sb.append(LS);
//        });
//        sb.append("java -cp %CLASSPATH% com.sdarioo.bddviewer.launcher.app.Main");
//        sb.append(LS);
//
//        Files.write(Paths.get("C:\\Temp\\cp.bat"), sb.toString().getBytes());


        String cp = ClasspathBuilder.pcmClasspathBuilder(rootDir).toString();

        cp += ";C:\\Temp\\lib\\bddviewer.jar";

        String[] cmd = { "java", "-cp", cp, "com.sdarioo.bddviewer.launcher.app.Main" };

        runCmdLine(cmd, s -> System.out.println(s));
    }

    private static void runCmdLine(String[] command, Consumer<String> out) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command, null, new File("C:\\Dev\\pcm_ij\\cm\\app\\cm\\"));

            readStreamAsync(process.getInputStream(), out);
            readStreamAsync(process.getErrorStream(), out);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void readStreamAsync(InputStream inputStream, Consumer<String> consumer) {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(() -> {
            String line;
            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = stdError.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                consumer.accept("Error reading process output: " + e.toString());
            }
        });
    }




    private static final String LS = System.getProperty("line.separator");
}
