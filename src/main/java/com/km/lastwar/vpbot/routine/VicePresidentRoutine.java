package com.km.lastwar.vpbot.routine;

import com.km.lastwar.vpbot.exception.FlListNotFound;
import com.km.lastwar.vpbot.exception.GameWindowNotFoundException;
import com.km.lastwar.vpbot.utils.GameWindowManager;
import com.km.lastwar.vpbot.utils.IconPositionDetector;
import com.km.lastwar.vpbot.utils.ScreenshotUtil;
import com.km.lastwar.vpbot.utils.ThreadUtil;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

import static com.km.lastwar.vpbot.data.Constants.DEBUG_SCREEN_PATH;
import static com.km.lastwar.vpbot.utils.IconPositionDetector.isIconOnScreenshot;

public class VicePresidentRoutine {

    private static final Logger logger = LoggerFactory.getLogger(VicePresidentRoutine.class);

    public void doRoutine() throws FlListNotFound {
        GameWindowManager.focusWindow(GameWindowManager.findGameWindow());

        int attempts = 5;
        boolean isOnMainPage = false;
        try {
            isOnMainPage = getOnMainPage(attempts);
            boolean isOnProfilePage = false;

            if (isOnMainPage) {
                isOnProfilePage = getOnProfilePage();

                if (isOnProfilePage) {
                    doVPRoutine();
                }
            }

            // TODO: let it crash or use logger?
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GameWindowNotFoundException e) {
            throw new RuntimeException(e);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private void doVPRoutine() {
    }

    private boolean getOnProfilePage() throws IOException, GameWindowNotFoundException, AWTException {
        clickOnLastDetectedIconPosition();
        ThreadUtil.pause(2000);

        if (isProfilePage()) {
            clickOnLastDetectedIconPosition();
            return true;
        }

        return false;
    }

    private boolean getOnMainPage(int attempts) throws IOException, GameWindowNotFoundException, AWTException {
        boolean isOnMainPage = false;

        for (int i = 0; i < attempts; i++) {
            if (hasCloseIcon()) {
                clickOnLastDetectedIconPosition();
                ThreadUtil.pause(2000);
            }

            if (hasBackIcon()) {
                clickOnLastDetectedIconPosition();
                ThreadUtil.pause(2000);
            }

            if (isMainPage()) {
                isOnMainPage = true;
                break;
            }
        }

        return isOnMainPage;
    }

    private void clickOnLastDetectedIconPosition() {
        WinDef.RECT rect = GameWindowManager.getInfo();

        if (rect == null) {
            logger.error("Emulator-Window not found!");
            return;
        }

        int screenX = rect.left + (int) IconPositionDetector.iconPositionX;
        int screenY = rect.top + (int) IconPositionDetector.iconPositionY;

        logger.info("Click on window position (%d, %d)%n", screenX, screenY);

        try {
            GameWindowManager.clickAt(screenX, screenY);
        } catch (AWTException e) {
            logger.error("Could not click on target");
        }
    }

    private boolean isMainPage() throws IOException, AWTException, GameWindowNotFoundException {
        logger.info("Main page detection");

        takeScreenshotFromGameWindow("main-page-check.png");

        boolean hasIcon = isIconOnScreenshot(
                DEBUG_SCREEN_PATH + "main-page-check.png",
                "src/main/resources/images/default-profile-pic-color.png",
                0.9,
                100
        );

        logger.info("Main page: " + hasIcon);

        return hasIcon;
    }

    private void takeScreenshotFromGameWindow(String fileName) throws GameWindowNotFoundException, AWTException, IOException {
        WinDef.RECT rect = GameWindowManager.getInfo();

        if (rect == null) {
            throw new GameWindowNotFoundException();
        }

        ScreenshotUtil.takeScreenshot(
                rect.left,
                rect.top,
                rect.right - rect.left,
                rect.bottom - rect.top,
                DEBUG_SCREEN_PATH,
                fileName
        );
    }

    private boolean isProfilePage() throws IOException, GameWindowNotFoundException, AWTException {
        logger.info("Profile page detection");

        takeScreenshotFromGameWindow("profile-page-check.png");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "profile-page-check.png",
                "src/main/resources/images/hash-icon.png",
                0.5,
                9999
        );

        logger.info("Profile page: " + hasIcon);

        return hasIcon;
    }

    private boolean hasCloseIcon() throws IOException, GameWindowNotFoundException, AWTException {
        logger.info("Close icon detection");

        takeScreenshotFromGameWindow("close-icon-check.png");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "close-icon-check.png",
                "src/main/resources/images/close-icon.png",
                0.7,
                9999
        );

        logger.info("Close icon: " + hasIcon);

        return hasIcon;
    }

    private boolean hasBackIcon() throws IOException, GameWindowNotFoundException, AWTException {
        logger.info("Back icon detection");

        takeScreenshotFromGameWindow("back-icon-check.png");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "back-icon-check.png",
                "src/main/resources/images/back-icon.png",
                0.7,
                9999
        );

        logger.info("Back icon: " + hasIcon);

        return hasIcon;
    }
}