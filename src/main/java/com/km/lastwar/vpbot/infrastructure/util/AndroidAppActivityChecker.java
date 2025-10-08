package com.km.lastwar.vpbot.infrastructure.util;

import com.km.lastwar.vpbot.domain.port.AppActivityChecker;
import com.km.lastwar.vpbot.infrastructure.AdbCommandExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AndroidAppActivityChecker implements AppActivityChecker {

    @Inject
    private AdbCommandExecutor adbCommandExecutor;

    @Override
    public boolean isAppRunning(String packageName) {

        try {
            String output = adbCommandExecutor.execute("shell", "pidof", packageName);
            return !output.trim().isEmpty();
        } catch (Exception e) {
            // TODO Add Exception handling or logging
            return false;
        }
    }
}