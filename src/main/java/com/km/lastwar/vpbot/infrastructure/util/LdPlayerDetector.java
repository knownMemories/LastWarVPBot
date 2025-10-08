package com.km.lastwar.vpbot.infrastructure.util;

import com.km.lastwar.vpbot.domain.model.EmulatorStatus;
import com.km.lastwar.vpbot.domain.port.EmulatorDetector;
import com.km.lastwar.vpbot.infrastructure.AdbCommandExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LdPlayerDetector implements EmulatorDetector {

    @Inject
    private AdbCommandExecutor adbExecutor;

    @Override
    public EmulatorStatus detectStatus() {
        String output = adbExecutor.execute("devices");

        String[] lines = output.split("\\r?\\n");
        boolean hasDevice = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals("List of devices attached")) {
                continue;
            }

            if (line.contains("device") && !line.contains("unauthorized")) {
                hasDevice = true;
                break;
            }
        }

        if (hasDevice) {
            System.out.println("✅ Emulator detected.");
            return EmulatorStatus.RUNNING;
        } else {
            System.out.println("❌ No emulator found in ADB devices.");
            return EmulatorStatus.NOT_RUNNING;
        }
    }
}