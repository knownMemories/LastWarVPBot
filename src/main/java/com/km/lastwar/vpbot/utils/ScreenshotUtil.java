package com.km.lastwar.vpbot.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility class to capture a screenshot of a specific screen region.
 */
public final class ScreenshotUtil {
    private ScreenshotUtil() {}

    /**
     * Captures a screenshot of a rectangular area on the primary screen
     * and saves it as a PNG file.
     *
     * @param x        X-coordinate of the top-left corner (in pixels)
     * @param y        Y-coordinate of the top-left corner (in pixels)
     * @param width    Width of the capture area (in pixels)
     * @param height   Height of the capture area (in pixels)
     * @param filePath Directory path where the image will be saved (e.g., "/home/user/screenshots")
     * @param fileName Filename without extension (e.g., "screenshot1") – ".png" will be appended automatically
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IOException              if an error occurs while saving the file
     * @throws AWTException             if the Robot cannot be initialized
     */
    public static void takeScreenshot(int x, int y, int width, int height, String filePath, String fileName)
            throws AWTException, IOException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than 0.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be null or empty.");
        }
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path must not be null or empty.");
        }

        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(x, y, width, height);
        BufferedImage screenshot = robot.createScreenCapture(screenRect);

        Path dir = Path.of(filePath);
        if (!dir.toFile().exists() && !dir.toFile().mkdirs()) {
            throw new IOException("Failed to create directory: " + filePath);
        }

        String fullFilePath = filePath + File.separator + fileName + ".png";
        File outputFile = new File(fullFilePath);

        if (!ImageIO.write(screenshot, "png", outputFile)) {
            throw new IOException("Failed to write image as PNG.");
        }
    }
}