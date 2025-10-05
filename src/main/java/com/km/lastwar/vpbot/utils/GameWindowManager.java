package com.km.lastwar.vpbot.utils;

import ch.qos.logback.classic.Logger;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;

public class GameWindowManager {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameWindowManager.class);
    private static final String EMULATOR_NAME = "LDPlayer";

    public static WinDef.HWND findGameWindow() {
        return User32.INSTANCE.FindWindow(null, EMULATOR_NAME);
    }

    public static WinDef.RECT getInfo() {
        WinDef.HWND hwnd = findGameWindow();

        if (hwnd != null) {
            WinDef.RECT rect = new WinDef.RECT();
            if (User32.INSTANCE.GetWindowRect(hwnd, rect)) {
                logger.info("LDPlayer found:");
                logger.info("X: " + rect.left);
                logger.info("Y: " + rect.top);
                logger.info("Width: " + (rect.right - rect.left));
                logger.info("Height: " + (rect.bottom - rect.top));

                return rect;
            } else {
                logger.info("Fehler beim Abrufen der Fenstergröße.");
            }

        } else {
            logger.info("Fenster nicht gefunden.");
        }

        return null;
    }

    public static Context findGameCoord(int screenIndex, BotType botType) {
        try {
            WinDef.RECT rect = GameWindowManager.getInfo();

            boolean gameInitialized = false;
            int attempts = 0;

            while (!gameInitialized) {
                if (rect == null) {
                    Utils.pause(10000);
                } else {
                    gameInitialized = true;
                }

                if (attempts > 3) {
                    return new Context(0, 0, 800, 600, screenIndex, botType);
                }

                attempts++;
            }

            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;

            return new Context(rect.left, rect.top, width, height, screenIndex, botType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void focusWindow(WinDef.HWND hwnd) {
        User32.INSTANCE.ShowWindow(hwnd, User32.SW_RESTORE);
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public static void clickAt(int x, int y) throws AWTException {
        Robot robot = new Robot();
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
