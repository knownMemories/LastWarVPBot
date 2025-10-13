package com.km.lastwar.vpbot.application;

import com.km.lastwar.vpbot.application.routine.VicePresidentUseCase;
import com.km.lastwar.vpbot.constants.GameProcess;
import com.km.lastwar.vpbot.domain.service.EmulatorMonitoringService;
import com.km.lastwar.vpbot.infrastructure.process.ProcessManager;
import com.km.lastwar.vpbot.infrastructure.util.DelayUtil;
import com.km.lastwar.vpbot.infrastructure.util.DirectoryInitializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationStarter {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    private final int gameCheckAttempts = 50;

    @Inject
    VicePresidentUseCase vicePresidentUseCase;

    @Inject
    DirectoryInitializer directoryInitializer;

    @Inject
    EmulatorMonitoringService emulatorMonitoringService;

    @Inject
    ProcessManager processManager;

    public void start() {

        logger.info("Initializing application...");
        directoryInitializer.init();

        processManager.startVPProcess();

        waitForEmulatorIsRunningAndGameStarted();

        try {
            vicePresidentUseCase.execute();
        } catch (Exception e) {
            logger.error("Application failed", e);
            throw new RuntimeException(e);
        }
    }

    private void waitForEmulatorIsRunningAndGameStarted() {

        for (int i = 0; i < gameCheckAttempts; i++) {
            boolean isRunning = emulatorMonitoringService.isGameRunning(GameProcess.GAME_PACKAGE_NAME);

            if (isRunning) {
                System.out.println("Game is running on LDPlayer!");
                break;
            }

            System.out.println("Game is not running on LDPlayer! Waiting..");
            DelayUtil.pause(3000);
        }
    }
}