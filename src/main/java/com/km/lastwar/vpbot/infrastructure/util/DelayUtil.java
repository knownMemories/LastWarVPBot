package com.km.lastwar.vpbot.infrastructure.util;

public class DelayUtil {

    public static void pause(int ms) {

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
