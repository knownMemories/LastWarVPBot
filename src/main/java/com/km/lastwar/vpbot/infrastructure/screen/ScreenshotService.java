package com.km.lastwar.vpbot.infrastructure.screen;

import com.km.lastwar.vpbot.domain.exception.GameWindowNotFoundException;
import com.km.lastwar.vpbot.infrastructure.process.GameWindowManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

@ApplicationScoped
public class ScreenshotService {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotService.class);

    @Inject
    GameWindowManager gameWindowManager;

    public BufferedImage captureGameWindow() throws GameWindowNotFoundException, AWTException {

        var rect = gameWindowManager.getWindowRect();
        if (rect == null) throw new GameWindowNotFoundException("Game window not found");

        Robot robot = new Robot();
        Rectangle area = new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
        return robot.createScreenCapture(area);
    }
}