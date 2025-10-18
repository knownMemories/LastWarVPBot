package com.km.lastwar.vpbot.infrastructure.image;

import jakarta.enterprise.context.ApplicationScoped;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class TemplateMatcher {

    private static final Logger logger = LoggerFactory.getLogger(TemplateMatcher.class);

    static {
        OpenCV.loadLocally();
    }

    public MatchResult match(BufferedImage screenshot, BufferedImage template, double threshold) {

        File screenFile = null;
        File templateFile = null;
        try {
            screenFile = File.createTempFile("screen", ".png");
            templateFile = File.createTempFile("template", ".png");
            ImageIO.write(screenshot, "png", screenFile);
            ImageIO.write(template, "png", templateFile);

            Mat screenMat = Imgcodecs.imread(screenFile.getAbsolutePath());
            Mat templateMat = Imgcodecs.imread(templateFile.getAbsolutePath());

            if (screenMat.empty() || templateMat.empty()) {
                logger.error("Failed to load images for matching");
                return new MatchResult(false, new Point(), 0.0);
            }

            Mat result = new Mat();
            Imgproc.matchTemplate(screenMat, templateMat, result, Imgproc.TM_CCOEFF_NORMED);
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

            boolean found = mmr.maxVal >= threshold;
            logger.info("Match result: confidence={}, found={}", mmr.maxVal, found);

            return new MatchResult(found, mmr.maxLoc, mmr.maxVal);
        } catch (IOException e) {
            logger.error("Matching failed", e);
            return new MatchResult(false, new Point(), 0.0);
        } finally {
            // Clean up temp files immediately after use
            if (screenFile != null) screenFile.delete();
            if (templateFile != null) templateFile.delete();
        }
    }

    public MatchResult matchTopmost(BufferedImage screenshot, BufferedImage template, double threshold) {

        File screenFile = null;
        File templateFile = null;
        try {
            screenFile = File.createTempFile("screen", ".png");
            templateFile = File.createTempFile("template", ".png");
            ImageIO.write(screenshot, "png", screenFile);
            ImageIO.write(template, "png", templateFile);

            Mat screenMat = Imgcodecs.imread(screenFile.getAbsolutePath());
            Mat templateMat = Imgcodecs.imread(templateFile.getAbsolutePath());

            if (screenMat.empty() || templateMat.empty()) {
                logger.error("Failed to load images for matching");
                return new MatchResult(false, new Point(), 0.0);
            }

            // Safety check: template must be smaller than screenshot
            if (templateMat.rows() > screenMat.rows() || templateMat.cols() > screenMat.cols()) {
                logger.warn("Template is larger than screenshot – no match possible");
                return new MatchResult(false, new Point(), 0.0);
            }

            Mat result = new Mat();
            Imgproc.matchTemplate(screenMat, templateMat, result, Imgproc.TM_CCOEFF_NORMED);

            Point bestPoint = null;
            double bestConfidence = 0.0;

            // Iterate over the entire result matrix to find all valid matches
            for (int y = 0; y < result.rows(); y++) {
                for (int x = 0; x < result.cols(); x++) {
                    double confidence = result.get(y, x)[0];
                    // Check if the match confidence meets or exceeds the threshold
                    if (confidence >= threshold) {
                        // Select the topmost match (smallest y-coordinate)
                        // In case of equal y, prefer the leftmost (smallest x) – optional but deterministic
                        if (bestPoint == null || y < bestPoint.y || (y == bestPoint.y && x < bestPoint.x)) {
                            bestPoint = new Point(x, y);
                            bestConfidence = confidence;
                        }
                    }
                }
            }

            boolean found = bestPoint != null;
            Point position = found ? bestPoint : new Point();
            logger.info("Topmost match: found={}, position=({}, {}), confidence={}",
                         found, position.x, position.y, bestConfidence
                        );

            return new MatchResult(found, position, bestConfidence);
        } catch (IOException e) {
            logger.error("Matching failed", e);
            return new MatchResult(false, new Point(), 0.0);
        } finally {
            // Clean up temp files immediately after use
            if (screenFile != null) screenFile.delete();
            if (templateFile != null) templateFile.delete();
        }
    }

    public record MatchResult(boolean found, Point position, double confidence) {

    }
}