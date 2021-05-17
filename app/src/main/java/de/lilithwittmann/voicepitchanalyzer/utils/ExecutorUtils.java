package de.lilithwittmann.voicepitchanalyzer.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtils {
    private static final ExecutorService BOUNDED = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> new Thread(r, "bounded-executor")
    );

    public static ExecutorService getBoundedExecutor() {
        return BOUNDED;
    }
}
