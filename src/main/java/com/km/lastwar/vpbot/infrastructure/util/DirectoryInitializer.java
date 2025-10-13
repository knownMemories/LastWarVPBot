package com.km.lastwar.vpbot.infrastructure.util;

import com.km.lastwar.vpbot.infrastructure.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

@ApplicationScoped
public class DirectoryInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryInitializer.class);

    @Inject
    AppConfig config;

    public void init() {

        createAndClearDirectories();
    }

    private void createAndClearDirectories() {

        String debugPath = config.getDebugScreenPath();
        FileManager.createDirectoryIfNotExists(debugPath);
        FileManager.deleteDirectoryContents(Paths.get(debugPath));
    }
}