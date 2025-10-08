package com.km.lastwar.vpbot.domain.port;

public interface AppActivityChecker {

    boolean isAppRunning(String packageName);
}