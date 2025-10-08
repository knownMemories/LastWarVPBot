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

        try {
            File screenFile = File.createTempFile("screen", ".png");
            File templateFile = File.createTempFile("template", ".png");
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
            logger.debug("Match result: confidence={}, found={}", mmr.maxVal, found);

            return new MatchResult(found, mmr.maxLoc, mmr.maxVal);
        } catch (IOException e) {
            logger.error("Matching failed", e);
            return new MatchResult(false, new Point(), 0.0);
        }
    }

    public record MatchResult(boolean found, org.opencv.core.Point position, double confidence) {

    }
}