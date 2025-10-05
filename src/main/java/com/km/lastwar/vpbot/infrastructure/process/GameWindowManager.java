package com.km.lastwar.vpbot.infrastructure.process;

import com.km.lastwar.vpbot.domain.exception.GameWindowNotFoundException;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;

@ApplicationScoped
public class GameWindowManager {

    private static final Logger logger = LoggerFactory.getLogger(GameWindowManager.class);
    private static final String EMULATOR_NAME = "LDPlayer";

    public WinDef.RECT getWindowRect() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, EMULATOR_NAME);
        if (hwnd == null) return null;

        WinDef.RECT rect = new WinDef.RECT();
        if (User32.INSTANCE.GetWindowRect(hwnd, rect)) {
            logger.debug("Window found: x={}, y={}, w={}, h={}",
                    rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
            return rect;
        }
        return null;
    }

    public void focusWindow() throws GameWindowNotFoundException {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, EMULATOR_NAME);
        if (hwnd == null) throw new GameWindowNotFoundException("LDPlayer window not found");
        User32.INSTANCE.ShowWindow(hwnd, User32.SW_RESTORE);
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public void clickAt(int x, int y) throws AWTException {
        Robot robot = new Robot();
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}