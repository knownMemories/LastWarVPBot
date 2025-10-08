package com.km.lastwar.vpbot.infrastructure.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    public static void createDirectoryIfNotExists(String path) {

        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            logger.error("Failed to create directory: {}", path);
        }
    }

    public static void deleteDirectoryContents(Path directory) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                if (attrs.isDirectory()) {
                    deleteDirectoryContents(entry);
                }
                Files.delete(entry);
            }
        } catch (IOException e) {
            logger.error("Failed to delete directory contents: {}", directory, e);
        }
    }
}