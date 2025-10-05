package com.km.lastwar.vpbot.infrastructure.io;

import com.km.lastwar.vpbot.domain.stats.Stats;
import com.km.lastwar.vpbot.infrastructure.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@ApplicationScoped
public class Serializer {

    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

    @Inject
    AppConfig config;

    public void saveStats(Stats stats) {
        String path = config.getStatsPath();
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(stats);
            logger.info("Stats saved to {}", path);
        } catch (IOException e) {
            logger.error("Failed to save stats to {}", path, e);
            throw new RuntimeException("Stats serialization failed", e);
        }
    }

    public Stats loadStats() {
        String path = config.getStatsPath();
        File file = new File(path);
        if (!file.exists()) {
            logger.info("No stats file found, returning initial stats");
            return Stats.initial();
        }
        try (FileInputStream fileIn = new FileInputStream(path);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            Stats stats = (Stats) objectIn.readObject();
            logger.info("Stats loaded from {}", path);
            return stats;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to load stats from {}", path, e);
            return Stats.initial();
        }
    }
}