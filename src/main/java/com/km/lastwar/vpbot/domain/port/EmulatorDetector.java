package com.km.lastwar.vpbot.domain.port;

import com.km.lastwar.vpbot.domain.model.EmulatorStatus;

public interface EmulatorDetector {

    EmulatorStatus detectStatus();
}