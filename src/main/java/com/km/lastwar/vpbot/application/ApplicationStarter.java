package com.km.lastwar.vpbot.application;

import com.km.lastwar.vpbot.application.routine.VicePresidentUseCase;
import com.km.lastwar.vpbot.infrastructure.util.DirectoryInitializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationStarter {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    @Inject
    VicePresidentUseCase vicePresidentUseCase;

    @Inject
    DirectoryInitializer directoryInitializer;

    public void start() {
        logger.info("Initializing application...");
        directoryInitializer.init();
        try {
            vicePresidentUseCase.execute();
        } catch (Exception e) {
            logger.error("Application failed", e);
            throw new RuntimeException(e);
        }
    }
}