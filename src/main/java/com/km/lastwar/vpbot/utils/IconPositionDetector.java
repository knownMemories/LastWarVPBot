package com.km.lastwar.vpbot.utils;

import ch.qos.logback.classic.Logger;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.LoggerFactory;

public class IconPositionDetector {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(IconPositionDetector.class);

    public static double iconPositionX = 0.0;
    public static double iconPositionY = 0.0;

    static {
        OpenCV.loadLocally();
    }

    public static boolean isIconOnScreenshot(String screenshotPath, String iconPath, double threshold, int maxDistance) {
        Mat screenshot = Imgcodecs.imread(screenshotPath);
        Mat icon = Imgcodecs.imread(iconPath);

        if (screenshot.empty() || icon.empty()) {
            System.err.println("Bilder konnten nicht geladen werden.");
            return false;
        }

        Mat result = new Mat();
        Imgproc.matchTemplate(screenshot, icon, result, Imgproc.TM_CCOEFF_NORMED);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLocation = mmr.maxLoc;
        double confidence = mmr.maxVal;

        System.out.printf("Bestes Match bei (%.0f, %.0f) mit Konfidenz: %.2f%n",
                matchLocation.x, matchLocation.y, confidence);

        boolean nearTopLeft = (matchLocation.x <= maxDistance) && (matchLocation.y <= maxDistance);
        boolean strongMatch = confidence >= threshold;

        if (strongMatch && nearTopLeft) {
            iconPositionX = matchLocation.x;
            iconPositionY = matchLocation.y;

            return true;
        }

        return false;
    }
}