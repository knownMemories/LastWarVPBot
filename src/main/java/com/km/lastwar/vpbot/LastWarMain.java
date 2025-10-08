package com.km.lastwar.vpbot;

import com.km.lastwar.vpbot.application.ApplicationStarter;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class LastWarMain {

    public static void main(String[] args) {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            ApplicationStarter starter = container.select(ApplicationStarter.class).get();
            starter.start();
        }
    }
}