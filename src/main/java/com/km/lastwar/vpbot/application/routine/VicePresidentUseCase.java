package com.km.lastwar.vpbot.application.routine;

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
public class VicePresidentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(VicePresidentUseCase.class);

    @Inject
    GameWindowManager gameWindowManager;
    @Inject
    ScreenshotService screenshotService;
    @Inject
    ImageRepository imageRepository;
    @Inject
    TemplateMatcher templateMatcher;

    public void execute() {
        try {
            gameWindowManager.focusWindow();

            if (!navigateToMainPage(5)) {
                throw new IllegalStateException("Could not reach main page");
            }

            if (!navigateToProfilePage()) {
                throw new IllegalStateException("Could not reach profile page");
            }

            performVPRoutine();
        } catch (GameWindowNotFoundException | AWTException e) {
            throw new RuntimeException("Navigation failed", e);
        }
    }

    private boolean navigateToMainPage(int maxAttempts) throws GameWindowNotFoundException, AWTException {
        for (int i = 0; i < maxAttempts; i++) {
            BufferedImage screen = screenshotService.captureGameWindow();

            if (isCloseIconVisible(screen)) {
                clickOnMatch(screen, "close-icon.png");
                pause(2000);
                continue;
            }

            if (isBackIconVisible(screen)) {
                clickOnMatch(screen, "back-icon.png");
                pause(2000);
                continue;
            }

            if (isMainPage(screen)) {
                return true;
            }

            pause(2000);
        }
        return false;
    }

    private boolean navigateToProfilePage() throws GameWindowNotFoundException, AWTException {
        clickOnMatch(screenshotService.captureGameWindow(), "default-profile-pic-color.png");
        pause(2000);
        return isProfilePage(screenshotService.captureGameWindow());
    }

    private boolean isMainPage(BufferedImage screen) {
        var icon = imageRepository.getImage("default-profile-pic-color.png");
        return templateMatcher.match(screen, icon.image(), 0.9).found();
    }

    private boolean isProfilePage(BufferedImage screen) {
        var icon = imageRepository.getImage("hash-icon.png");
        return templateMatcher.match(screen, icon.image(), 0.5).found();
    }

    private boolean isCloseIconVisible(BufferedImage screen) {
        var icon = imageRepository.getImage("close-icon.png");
        return templateMatcher.match(screen, icon.image(), 0.7).found();
    }

    private boolean isBackIconVisible(BufferedImage screen) {
        var icon = imageRepository.getImage("back-icon.png");
        return templateMatcher.match(screen, icon.image(), 0.7).found();
    }

    private void clickOnMatch(BufferedImage screen, String iconName) throws GameWindowNotFoundException, AWTException {
        var icon = imageRepository.getImage(iconName);
        var result = templateMatcher.match(screen, icon.image(), 0.7);
        if (result.found()) {
            var rect = gameWindowManager.getWindowRect();
            int screenX = rect.left + (int) result.position().x;
            int screenY = rect.top + (int) result.position().y;
            gameWindowManager.clickAt(screenX, screenY);
            logger.info("Clicked at ({}, {})", screenX, screenY);
        }
    }

    private void performVPRoutine() {
        logger.info("Performing VP routine...");

    }

    private void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}