package com.km.lastwar.vpbot.application.routine.service;

import com.km.lastwar.vpbot.domain.exception.GameWindowNotFoundException;
import com.km.lastwar.vpbot.infrastructure.image.ImageRepository;
import com.km.lastwar.vpbot.infrastructure.image.TemplateMatcher;
import com.km.lastwar.vpbot.infrastructure.screen.ScreenshotService;
import com.km.lastwar.vpbot.infrastructure.util.DelayUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.km.lastwar.vpbot.constants.VpBotConstants.BACK_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.CLOSE_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.CROWN_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.DEFAULT_MATCH_THRESHOLD;
import static com.km.lastwar.vpbot.constants.VpBotConstants.HASH_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.HASH_MATCH_THRESHOLD;
import static com.km.lastwar.vpbot.constants.VpBotConstants.MEDIUM_PAUSE_MS;
import static com.km.lastwar.vpbot.constants.VpBotConstants.PROFILE_MATCH_THRESHOLD;
import static com.km.lastwar.vpbot.constants.VpBotConstants.PROFILE_PIC_ICON;
import static com.km.lastwar.vpbot.constants.VpBotConstants.SHORT_PAUSE_MS;
import static com.km.lastwar.vpbot.constants.VpBotConstants.VERY_SHORT_PAUSE_MS;

@ApplicationScoped
public class NavigationService {

    @Inject
    ClickService clickService;

    @Inject
    ScreenshotService screenshotService;

    @Inject
    ImageRepository imageRepository;

    @Inject
    TemplateMatcher templateMatcher;

    public boolean navigateToMainPage(int maxAttempts) throws GameWindowNotFoundException, AWTException {

        for (int i = 0; i < maxAttempts; i++) {
            BufferedImage screen = screenshotService.captureGameWindow();

            if (isCloseIconVisible(screen)) {
                clickCloseIcon();
                DelayUtil.pause(VERY_SHORT_PAUSE_MS);
                continue;
            }

            if (isBackIconVisible(screen)) {
                clickBackIcon();
                DelayUtil.pause(SHORT_PAUSE_MS);
                continue;
            }

            if (isMainPage(screen)) {
                return true;
            }

            DelayUtil.pause(MEDIUM_PAUSE_MS);
        }

        return false;
    }

    public boolean navigateToProfilePage() throws GameWindowNotFoundException, AWTException {

        clickProfileIcon();
        DelayUtil.pause(SHORT_PAUSE_MS);
        return isProfilePage(screenshotService.captureGameWindow());
    }

    public boolean navigateToVicePresidentPage() throws GameWindowNotFoundException, AWTException {

        clickHashIcon();
        DelayUtil.pause(SHORT_PAUSE_MS);
        return isMinistryPage(screenshotService.captureGameWindow());
    }

    public boolean isOnMinistryPage() throws GameWindowNotFoundException, AWTException {

        return isMinistryPage(screenshotService.captureGameWindow());
    }

    private boolean isMainPage(BufferedImage screen) {

        var icon = imageRepository.getImage(PROFILE_PIC_ICON);
        return templateMatcher.match(screen, icon.image(), PROFILE_MATCH_THRESHOLD).found();
    }

    private boolean isProfilePage(BufferedImage screen) {

        var icon = imageRepository.getImage(HASH_ICON);
        return templateMatcher.match(screen, icon.image(), HASH_MATCH_THRESHOLD).found();
    }

    private boolean isMinistryPage(BufferedImage screen) {

        var icon = imageRepository.getImage(CROWN_ICON);
        return templateMatcher.match(screen, icon.image(), 0.55).found();
    }

    private boolean isCloseIconVisible(BufferedImage screen) {

        var icon = imageRepository.getImage(CLOSE_ICON);
        return templateMatcher.match(screen, icon.image(), DEFAULT_MATCH_THRESHOLD).found();
    }

    private boolean isBackIconVisible(BufferedImage screen) {

        var icon = imageRepository.getImage(BACK_ICON);
        return templateMatcher.match(screen, icon.image(), DEFAULT_MATCH_THRESHOLD).found();
    }

    private void clickProfileIcon() throws AWTException, GameWindowNotFoundException {

        clickService.clickIfFound(PROFILE_PIC_ICON, DEFAULT_MATCH_THRESHOLD, 0, 0);
    }

    private void clickHashIcon() throws AWTException, GameWindowNotFoundException {

        clickService.clickIfFound(HASH_ICON, 0.6, 0, 0);
    }

    private void clickCloseIcon() throws AWTException, GameWindowNotFoundException {

        clickService.clickIfFound(CLOSE_ICON, DEFAULT_MATCH_THRESHOLD, 0, 0);
    }

    private void clickBackIcon() throws AWTException, GameWindowNotFoundException {

        clickService.clickIfFound(BACK_ICON, DEFAULT_MATCH_THRESHOLD, 0, 0);
    }
}