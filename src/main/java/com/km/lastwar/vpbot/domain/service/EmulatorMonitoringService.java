package com.km.lastwar.vpbot.domain.service;

import com.km.lastwar.vpbot.domain.model.EmulatorStatus;
import com.km.lastwar.vpbot.domain.port.AppActivityChecker;
import com.km.lastwar.vpbot.domain.port.EmulatorDetector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmulatorMonitoringService {

    @Inject
    private EmulatorDetector emulatorDetector;

    @Inject
    private AppActivityChecker appActivityChecker;

    public boolean isGameRunning(String packageName) {

        EmulatorStatus status = emulatorDetector.detectStatus();
        if (status != EmulatorStatus.RUNNING) {
            return false;
        }

        return appActivityChecker.isAppRunning(packageName);
    }
}