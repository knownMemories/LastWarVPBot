package com.km.lastwar.vpbot;

import com.km.lastwar.vpbot.data.Constants;
import com.km.lastwar.vpbot.data.Stats;
import com.km.lastwar.vpbot.exception.FlListNotFound;
import com.km.lastwar.vpbot.routine.VicePresidentRoutine;
import com.km.lastwar.vpbot.thread.ScheduledLauncher;
import com.km.lastwar.vpbot.utils.FileManager;
import com.km.lastwar.vpbot.utils.ProcessManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

@ApplicationScoped
public class LastWarRunnable {

    private static final Logger logger = LoggerFactory.getLogger(LastWarRunnable.class);

    @Inject
    private VicePresidentRoutine vicePresidentRoutine;

    public void run() {
        init();

        ScheduledLauncher scheduledLauncher = new ScheduledLauncher();
        scheduledLauncher.autoRestartBot(new Stats());

        try {
            vicePresidentRoutine.doRoutine();
        } catch (FlListNotFound e) {
            logger.error("firstLadySingleRoutine exception %s".formatted(e.getMessage()));
        }
    }

    private static void init() {
        createAndClearDirectories();
        ProcessManager.startVPProcess();
    }

    private static void createAndClearDirectories() {
        FileManager.createDirectoryIfNotExists(Constants.DEBUG_SCREEN_PATH);
        FileManager.deleteDirectoryContents(Paths.get(Constants.DEBUG_SCREEN_PATH));
    }
}
