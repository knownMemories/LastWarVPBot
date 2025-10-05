package com.km.lastwar.vpbot.infrastructure.image;

import com.km.lastwar.vpbot.infrastructure.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ImageRepository {

    private static final Logger logger = LoggerFactory.getLogger(ImageRepository.class);

    private final Map<String, ImageWrapper> cache = new ConcurrentHashMap<>();
    private final AppConfig config;

    @Inject
    public ImageRepository(AppConfig config) {
        this.config = config;
    }

    public ImageWrapper getImage(String resourceName) {
        return cache.computeIfAbsent(resourceName, this::loadFromResource);
    }

    private ImageWrapper loadFromResource(String resourceName) {
        String fullPath = config.getProjectImagesPath() + resourceName;
        try (InputStream is = getClass().getResourceAsStream(fullPath)) {
            if (is == null) {
                throw new RuntimeException("Image not found in resources: " + fullPath);
            }
            BufferedImage img = ImageIO.read(is);
            logger.info("Loaded image: {}", fullPath);
            return new ImageWrapper(resourceName, img);
        } catch (IOException e) {
            logger.error("Failed to load image: {}", fullPath, e);
            throw new RuntimeException("Image loading failed: " + fullPath, e);
        }
    }
}