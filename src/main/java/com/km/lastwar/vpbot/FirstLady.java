package com.km.lastwar.vpbot;

import ch.qos.logback.classic.Logger;
import com.km.lastwar.vpbot.data.BotType;
import com.km.lastwar.vpbot.data.Stats;
import com.km.lastwar.vpbot.exception.FlListNotFound;
import com.km.lastwar.vpbot.io.Frame;
import com.km.lastwar.vpbot.io.Mouse;
import com.km.lastwar.vpbot.io.Screen;
import com.km.lastwar.vpbot.utils.GameWindowManager;
import com.km.lastwar.vpbot.utils.IconPositionDetector;
import com.km.lastwar.vpbot.utils.Utils;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.km.lastwar.vpbot.data.Constants.DEBUG_SCREEN_PATH;
import static com.km.lastwar.vpbot.utils.IconPositionDetector.isIconOnScreenshot;

public class FirstLady {

    private static final Logger logger = (Logger) LoggerFactory.getLogger("FL");

    private Mouse mouseFl;
    private Screen screenFl;

    public FirstLady(Mouse mouseFl, Screen screenFl) {
        this.mouseFl = mouseFl;
        this.screenFl = screenFl;
    }

    public void firstLadySingleRoutine(Stats stats) throws FlListNotFound {
        Frame.updateCurrentBot(BotType.FL);
        GameWindowManager.focusWindow(GameWindowManager.findGameWindow());

        int attempts = 5;
        boolean isOnMainPage = false;

        for (int i = 0; i < attempts; i++) {
            if (hasCloseIcon()) {
                clickOnLastDetectedIconPosition();
                Utils.pause(2000);
            }

            if (isMainPage()) {
                isOnMainPage = true;
                break;
            }
        }

        if (isOnMainPage) {
            clickOnLastDetectedIconPosition();
            Utils.pause(2000);

            if (isProfilePage()) {
                clickOnLastDetectedIconPosition();
            }
        }
    }

    private void clickOnLastDetectedIconPosition() {
        WinDef.RECT rect = GameWindowManager.getInfo();

        if (rect == null) {
            logger.error("Emulator-Fenster nicht gefunden!");
            return;
        }

        int screenX = rect.left + (int) IconPositionDetector.iconPositionX;
        int screenY = rect.top + (int) IconPositionDetector.iconPositionY;

        logger.info("Klicke auf Bildschirmposition (%d, %d)%n", screenX, screenY);

        try {
            GameWindowManager.clickAt(screenX, screenY);
        } catch (AWTException e) {
            logger.error("Could not click on target");
        }
    }

    private boolean isMainPage() {
        logger.info("Main page detection");
        Screen.getInstance(BotType.FL).takeScreenShot("main-page-check");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "main-page-check.png",
                "src/main/resources/images/default-profile-pic-color.png",
                0.9,
                100
        );

        logger.info("Main page: " + hasIcon);

        return hasIcon;
    }

    private boolean isProfilePage() {
        logger.info("Profile page detection");
        Screen.getInstance(BotType.FL).takeScreenShot("profile-page-check");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "profile-page-check.png",
                "src/main/resources/images/hash-icon.png",
                0.5,
                9999
        );

        logger.info("Profile page: " + hasIcon);

        return hasIcon;
    }

    private boolean hasCloseIcon() {
        logger.info("Close icon detection");
        Screen.getInstance(BotType.FL).takeScreenShot("close-icon-check");

        boolean hasIcon = isIconOnScreenshot(DEBUG_SCREEN_PATH + "close-icon-check.png",
                "src/main/resources/images/close-icon.png",
                0.7,
                9999
        );

        logger.info("Close icon: " + hasIcon);

        return hasIcon;
    }

    public Mouse getMouseFl() {
        return mouseFl;
    }

    public void setMouseFl(Mouse mouseFl) {
        this.mouseFl = mouseFl;
    }

    public Screen getScreenFl() {
        return screenFl;
    }

    public void setScreenFl(Screen screenFl) {
        this.screenFl = screenFl;
    }

    @Override
    public String toString() {
        return "FirstLady [mouseFl=" + mouseFl + ", screenFl=" + screenFl + "]";
    }
}