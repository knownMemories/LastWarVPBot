package com.km.lastwar.vpbot.application.routine.service;

import com.km.lastwar.vpbot.infrastructure.process.GameWindowManager;
import com.km.lastwar.vpbot.infrastructure.util.DelayUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Random;

@ApplicationScoped
public class HumanBehaviorService {

    private static final Logger logger = LoggerFactory.getLogger(HumanBehaviorService.class);

    private static final int SCROLL_CHANCE_PERCENT = 5;   // 25% chance to perform scroll sequence
    private static final int DISTRACTION_CHANCE_PERCENT = 5; // 5% chance for neutral distraction click

    private final Random random = new Random();

    @Inject
    GameWindowManager gameWindowManager;

    /**
     * Simulates natural pre-click human behavior:
     * - Rare neutral tap (distraction)
     * - Occasional scroll + (mostly) scroll-back
     * - Natural delays
     */
    public void actNaturallyBeforeClick() throws AWTException {
        try {
//            if (random.nextInt(100) < DISTRACTION_CHANCE_PERCENT) {
//                performDistraction();
//                humanDelay(300, 600);
//            }

            if (random.nextInt(100) < SCROLL_CHANCE_PERCENT) {
                logger.info("Perform Random ScrollSequence");
                performRandomScrollSequence();
                humanDelay(400, 1000);
            }

            humanDelay(200, 400);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Human behavior interrupted", e);
        }
    }

    /**
     * Performs a scroll in a random direction, then (usually) scrolls back.
     * No clicking involved – safe for UIs with sensitive buttons.
     */
    private void performRandomScrollSequence() throws AWTException {
        Robot robot = new Robot();
        var rect = gameWindowManager.getWindowRect();
        if (rect != null) {
            int centerX = rect.left + (rect.right - rect.left) / 2;
            int centerY = rect.top + (rect.bottom - rect.top) / 2;
            robot.mouseMove(centerX, centerY);
        }

        boolean scrollUp = random.nextBoolean();
        int wheelAmount = 1 + random.nextInt(3); // 1 to 3 notches

        // Step 1: Scroll in random direction
        int firstScroll = scrollUp ? -wheelAmount : wheelAmount;
        logger.info("Initial scroll: {} (amount: {})", scrollUp ? "up" : "down", wheelAmount);
        robot.mouseWheel(firstScroll);

        try {
            Thread.sleep(200 + random.nextInt(300)); // natural pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Step 2: Usually scroll back (70% of the time)
        if (random.nextInt(100) < 70) {
            int scrollBack = -firstScroll;
            logger.info("Scrolling back (amount: {})", wheelAmount);
            robot.mouseWheel(scrollBack);

            // Optional: add tiny residual offset to avoid perfect return (feels more human)
            if (random.nextBoolean()) {
                int jitter = random.nextInt(2); // 0 or 1
                int finalJitter = scrollUp ? jitter : -jitter;
                if (finalJitter != 0) {
                    robot.mouseWheel(finalJitter);
                    logger.info("Added residual scroll jitter: {}", finalJitter);
                }
            }
        } else {
            // 30%: stay in the new scroll position – like a real player who continues browsing
            logger.info("Decided to stay in scrolled position");
        }
    }

    /**
     * Performs a safe, neutral click on a non-interactive area (e.g., top UI bar).
     */
    private void performDistraction() throws AWTException, InterruptedException {
        var rect = gameWindowManager.getWindowRect();
        if (rect == null) return;

        int distractX = rect.left + 100 + random.nextInt(200);
        int distractY = rect.top + 40 + random.nextInt(30);

        logger.info("Distraction click at ({}, {})", distractX, distractY);
        gameWindowManager.clickAt(distractX, distractY);
    }

    private void humanDelay(int minMs, int maxMs) throws InterruptedException {
        int delay = minMs + random.nextInt(maxMs - minMs + 1);
        DelayUtil.pause(delay);
    }
}