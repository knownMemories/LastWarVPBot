package com.km.lastwar.vpbot.application.routine.service;

import com.km.lastwar.vpbot.domain.exception.GameWindowNotFoundException;
import com.km.lastwar.vpbot.infrastructure.image.ImageRepository;
import com.km.lastwar.vpbot.infrastructure.image.TemplateMatcher;
import com.km.lastwar.vpbot.infrastructure.process.GameWindowManager;
import com.km.lastwar.vpbot.infrastructure.screen.ScreenshotService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

@ApplicationScoped
public class ClickService {

    private static final Logger logger = LoggerFactory.getLogger(ClickService.class);

    @Inject
    GameWindowManager gameWindowManager;

    @Inject
    ScreenshotService screenshotService;

    @Inject
    ImageRepository imageRepository;

    @Inject
    TemplateMatcher templateMatcher;

    public boolean clickIfFound(String iconName, double threshold, int offsetX, int offsetY) throws GameWindowNotFoundException, AWTException {

        BufferedImage screen = screenshotService.captureGameWindow();
        var icon = imageRepository.getImage(iconName);
        var match = templateMatcher.match(screen, icon.image(), threshold);

        if (match.found()) {
            var rect = gameWindowManager.getWindowRect();
            int screenX = rect.left + (int) match.position().x + offsetX;
            int screenY = rect.top + (int) match.position().y + offsetY;
            gameWindowManager.clickAt(screenX, screenY);
            logger.info("Clicked on {} at ({}, {})", iconName, screenX, screenY);
            return true;
        }

        return false;
    }

    public boolean testScreenshot(String iconName, String screenshotName) {

        var screenshot = imageRepository.getImage(screenshotName);
        var icon = imageRepository.getImage(iconName);

        var match = templateMatcher.match(screenshot.image(), icon.image(), 0.5);

        return match.found();
    }
}