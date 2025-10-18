package com.km.lastwar.vpbot.application.routine.service;

import com.km.lastwar.vpbot.constants.GameProcess;
import com.km.lastwar.vpbot.domain.exception.GameWindowNotFoundException;
import com.km.lastwar.vpbot.infrastructure.image.ImageRepository;
import com.km.lastwar.vpbot.infrastructure.image.TemplateMatcher;
import com.km.lastwar.vpbot.infrastructure.process.GameWindowManager;
import com.km.lastwar.vpbot.infrastructure.screen.ScreenshotService;
import com.km.lastwar.vpbot.infrastructure.util.DelayUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@ApplicationScoped
public class ClickService {

    private static final Logger logger = LoggerFactory.getLogger(ClickService.class);

    private static final double DEFAULT_TEST_THRESHOLD = 0.5;

    private final Random random = new Random();

    @Inject
    GameWindowManager gameWindowManager;

    @Inject
    ScreenshotService screenshotService;

    @Inject
    ImageRepository imageRepository;

    @Inject
    TemplateMatcher templateMatcher;

    @Inject
    HumanBehaviorService humanBehavior;

    /**
     * Clicks on the first (best-confidence) occurrence of the specified icon if its match confidence
     * meets or exceeds the given threshold.
     */
    public boolean clickIfFound(String iconName, double threshold, int offsetX, int offsetY)
            throws GameWindowNotFoundException, AWTException {

        return clickIfMatchFound(iconName, offsetX, offsetY,
                                 (screen, template) -> templateMatcher.match(screen, template, threshold)
                                );
    }

    /**
     * Clicks on the topmost (smallest Y, then smallest X) valid match of the icon
     * that meets or exceeds the given similarity threshold.
     */
    public boolean clickOnTopmostIfFound(String iconName, double threshold, int offsetX, int offsetY)
            throws GameWindowNotFoundException, AWTException {

        return clickIfMatchFound(iconName, offsetX, offsetY,
                                 (screen, template) -> templateMatcher.matchTopmost(screen, template, threshold)
                                );
    }

    /**
     * Executes the common click workflow:
     * - Captures screenshot
     * - Loads icon
     * - Applies the given matching strategy
     * - Calculates absolute screen coordinates
     * - Performs the click and logs the action
     */
    private boolean clickIfMatchFound(
            String iconName,
            int offsetX,
            int offsetY,
            java.util.function.BiFunction<BufferedImage, BufferedImage, TemplateMatcher.MatchResult> matcher)
            throws GameWindowNotFoundException, AWTException {

        humanBehavior.actNaturallyBeforeClick();

        BufferedImage screen = screenshotService.captureGameWindow();
        var imageWrapper = imageRepository.getImage(iconName);
        TemplateMatcher.MatchResult match = matcher.apply(screen, imageWrapper.image());

        if (match.found()) {
            var rect = gameWindowManager.getWindowRect();
            if (rect == null) {
                throw new GameWindowNotFoundException("Game window not found during click");
            }

            int iconW = imageWrapper.image().getWidth();
            int iconH = imageWrapper.image().getHeight();

            // Click randomly inside the matched icon area
            int randomX = (iconW > 1) ? random.nextInt(iconW) : 0;
            int randomY = (iconH > 1) ? random.nextInt(iconH) : 0;

            int screenX = rect.left + (int) match.position().x + randomX + offsetX;
            int screenY = rect.top + (int) match.position().y + randomY + offsetY;

            // Extra jitter for very small icons
            if (iconW <= 10 && iconH <= 10) {
                screenX += random.nextInt(7) - 3;
                screenY += random.nextInt(7) - 3;
            }

            DelayUtil.pause(100);
            gameWindowManager.clickAt(screenX, screenY);
            logger.info("Clicked inside '{}' at ({}, {})", iconName, screenX, screenY);
            return true;
        }

        return false;
    }

    public void clickOnCoord(int x, int y) throws AWTException, GameWindowNotFoundException {
        var rect = gameWindowManager.getWindowRect();
        if (rect == null) {
            throw new GameWindowNotFoundException("Game window not found during click");
        }

        gameWindowManager.clickAt(rect.left + x, rect.top + y);
    }

    /**
     * Tests whether the given icon appears in a saved screenshot using a default similarity threshold.
     */
    public boolean testScreenshot(String iconName, String screenshotName) {

        var screenshot = imageRepository.getImage(screenshotName);
        var icon = imageRepository.getImage(iconName);
        var match = templateMatcher.match(screenshot.image(), icon.image(), DEFAULT_TEST_THRESHOLD);
        return match.found();
    }
}