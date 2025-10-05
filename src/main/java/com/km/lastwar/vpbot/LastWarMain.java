package com.km.lastwar.vpbot;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastWarMain {

    private static final Logger logger = LoggerFactory.getLogger(LastWarMain.class);

    public static volatile boolean PAUSE = false;

    public static void main(String[] args) {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            LastWarRunnable lastWarRunnable = container.select(LastWarRunnable.class).get();
            lastWarRunnable.run();
        }
    }
}
