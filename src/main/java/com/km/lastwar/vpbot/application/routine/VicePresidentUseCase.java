package com.km.lastwar.vpbot.application.routine;

import com.km.lastwar.vpbot.application.routine.service.BotControlService;
import com.km.lastwar.vpbot.application.routine.service.ClickService;
import com.km.lastwar.vpbot.application.routine.service.NavigationService;
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

import static com.km.lastwar.vpbot.constants.VpBotConstants.ACCEPT_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.CLOSE_MINISTRY_LIST;
import static com.km.lastwar.vpbot.constants.VpBotConstants.MINISTRY_MATCH_THRESHOLD;
import static com.km.lastwar.vpbot.constants.VpBotConstants.DEFAULT_MATCH_THRESHOLD;
import static com.km.lastwar.vpbot.constants.VpBotConstants.EXCLAMATION_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.LIST_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.MEDIUM_PAUSE_MS;
import static com.km.lastwar.vpbot.constants.VpBotConstants.PAUSE_BETWEEN_USER_REQUESTS_MS;
import static com.km.lastwar.vpbot.constants.VpBotConstants.SHORT_PAUSE_MS;
import static com.km.lastwar.vpbot.constants.VpBotConstants.VERY_SHORT_PAUSE_MS;

@ApplicationScoped
public class VicePresidentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(VicePresidentUseCase.class);

    @Inject
    BotControlService botControl;

    @Inject
    GameWindowManager gameWindowManager;

    @Inject
    NavigationService navigation;

    @Inject
    ClickService clicker;

    @Inject
    ScreenshotService screenshotService;

    @Inject
    ImageRepository imageRepository;

    @Inject
    TemplateMatcher templateMatcher;

    public void execute() {

        botControl.reset();

        try {
            gameWindowManager.focusWindow();

            for (int i = 0; i < 20; i++) {
                if (navigation.navigateToMainPage(40)) {
                    if (!navigation.navigateToProfilePage()) {
                        continue;
                    }

                    if (navigation.navigateToVicePresidentPage()) {
                        break;
                    }
                }
            }

            performVPRoutine();
        } catch (GameWindowNotFoundException | AWTException e) {
            throw new RuntimeException("Navigation failed", e);
        } finally {
            logger.info("VP routine finished or stopped.");
        }
    }

    private void performVPRoutine() throws GameWindowNotFoundException, AWTException {

        logger.info("Performing VP routine...");
//        while (navigation.isOnMinistryPage()) {
        while (true) {
            if (botControl.shouldStop()) {
                logger.info("Stopping VP routine due to user request (ESC).");
                return;
            }

            if (botControl.isPaused()) {
                DelayUtil.pause(VERY_SHORT_PAUSE_MS);
                continue;
            }

            processMinistryRequests();
        }
    }

    private void processMinistryRequests() throws GameWindowNotFoundException, AWTException {
        // Open ministry page with user requests
        if (clicker.clickIfFound(EXCLAMATION_ICON, MINISTRY_MATCH_THRESHOLD, 20, 20)) {
            logger.info("#### User request found!");
            DelayUtil.pause(VERY_SHORT_PAUSE_MS);

            // open list
            logger.info("Open request list.");
            clicker.clickIfFound(LIST_ICON, MINISTRY_MATCH_THRESHOLD, 5, 5);
            DelayUtil.pause(VERY_SHORT_PAUSE_MS);

            // accept all
            logger.info("Trying to accept all applications!");
            acceptAllPending();

            // go back to ministry page
            logger.info("Going back to ministry page.");
            clicker.clickIfFound(CLOSE_MINISTRY_LIST, 0.67, 5, 5);
            DelayUtil.pause(VERY_SHORT_PAUSE_MS);
            clicker.clickIfFound(CLOSE_MINISTRY_LIST, 0.67, 5, 5);
        } else {
            logger.info("No user request found!");
        }

        DelayUtil.pause(PAUSE_BETWEEN_USER_REQUESTS_MS);
    }

    private void acceptAllPending() throws GameWindowNotFoundException, AWTException {

        while (isItemVisible(ACCEPT_ICON, 0.55)) {
            if (botControl.shouldStop()) {
                logger.info("Stopping accept loop due to user request (ESC).");
                return;
            }

            if (botControl.isPaused()) {
                DelayUtil.pause(MEDIUM_PAUSE_MS);
                continue;
            }

            clicker.clickOnTopmostIfFound(ACCEPT_ICON, 0.55, 15, 15);
            DelayUtil.pause(VERY_SHORT_PAUSE_MS);
        }
    }

    private boolean isItemVisible(String iconPath, double threshold) throws GameWindowNotFoundException, AWTException {

        BufferedImage screen = screenshotService.captureGameWindow();
        var icon = imageRepository.getImage(iconPath);
        TemplateMatcher.MatchResult match = templateMatcher.match(screen, icon.image(), threshold);
        return match.found();
    }
}