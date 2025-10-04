package com.km.lastwar.vpbot;

import bazmar.lastwar.autofl.data.*;
import com.km.lastwar.vpbot.data.*;
import com.knownmemories.lastwar.vpbot.data.*;
import en.knownmemories.lastwar.vpbot.data.*;
import com.km.lastwar.vpbot.exception.FlListNotFound;
import com.km.lastwar.vpbot.io.Frame;
import com.km.lastwar.vpbot.io.Mouse;
import com.km.lastwar.vpbot.io.Screen;
import com.km.lastwar.vpbot.io.Serializer;
import com.km.lastwar.vpbot.thread.ScheduledLauncher;
import com.km.lastwar.vpbot.utils.FileManager;
import com.km.lastwar.vpbot.utils.GameWindowManager;
import com.km.lastwar.vpbot.utils.ProcessManager;
import com.km.lastwar.vpbot.utils.Utils;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.nio.file.Paths;
import java.util.Date;

public class LastWarMain {

    private static final Logger logger = (Logger) LoggerFactory.getLogger("App");

    private static Mouse mouseScreen1;
    private static Mouse mouseScreen2;
    private static Mouse mouseFl;
    private static Screen screenFl;
    public static Context contextFL;

    public static int PAUSE_BETWEEN_FL_ROUTINE = 1000;
    public static int PAUSE_BETWEEN_FL_ACTION = 1000;
    public static volatile boolean PAUSE = false;

    private static FirstLady fl;

    private static final Stats stats = Serializer.loadStats();

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Frame::createAndShowGUI);

        mouseScreen1 = Mouse.getInstance(Constants.DEFAULT_CONTEXT);
        mouseScreen2 = Mouse.getInstance(Constants.DEFAULT_CONTEXT_2);

        stats.setLastFlAction(new Date());
        init(stats);
        reloadContexts();

        Utils.updateLogs();
        ScheduledLauncher scheduledLauncher = new ScheduledLauncher();
        scheduledLauncher.autoRestartBot(stats);
        Frame.updateLocation(contextFL.origX() + contextFL.width() + 40, 0, contextFL.screenIndex());

        firstLadyRoutine(stats);
    }

    private static void firstLadyRoutine(Stats stats) {

        try {
            fl.firstLadySingleRoutine(stats);
        } catch (FlListNotFound e) {
            logger.error("firstLadySingleRoutine exception %s".formatted(e.getMessage()));
        }

        Utils.pause(PAUSE_BETWEEN_FL_ROUTINE);
    }

    public static void reloadContexts() {

        if (loadFlContext()) {
            if (fl == null) {
                fl = new FirstLady(mouseFl, screenFl);
            } else {
                fl.setMouseFl(mouseFl);
                fl.setScreenFl(screenFl);
            }

            Zones.loadZoneWithContextFL(contextFL);
            logger.info("reloadContexts fl=%s".formatted(fl));
        }

        Frame.updateLocation(contextFL.origX() + contextFL.width() + 40, 0, contextFL.screenIndex());

    }

    private static void init(Stats stats) {
        // Remove existing handlers attached to the j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // Add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.install();

        logger.info("init default context is " + Constants.DEFAULT_CONTEXT);

        FileManager.createDirectoryIfNotExists(Constants.SANDBOX_PATH);
        FileManager.createDirectoryIfNotExists(Constants.MOUSE_SCREEN_PATH);
        FileManager.createDirectoryIfNotExists(Constants.DEBUG_SCREEN_PATH);
        FileManager.createDirectoryIfNotExists(Constants.KICK_SCREEN_PATH);
        FileManager.createDirectoryIfNotExists(Constants.TMP_SCREEN_PATH);

        FileManager.deleteDirectoryContents(Paths.get(Constants.DEBUG_SCREEN_PATH));
        FileManager.deleteDirectoryContents(Paths.get(Constants.MOUSE_SCREEN_PATH));
        FileManager.deleteDirectoryContents(Paths.get(Constants.TMP_SCREEN_PATH));

        ProcessManager.startBotFL();
//        Utils.pause(180000);

        boolean flInitialized = false;
        while (!flInitialized) {
            stats.setFlInitialized(loadFlContext());
            if (contextFL != null) {
                flInitialized = true;
            } else {
                ProcessManager.startBotFL();
                Utils.pause(30000);
            }
        }

        Frame.updateLocation(contextFL.origX() + contextFL.width() + 40, 0, contextFL.screenIndex());
    }

    private static boolean loadFlContext() {
        Context tmpContextFl = searchFlContext();

        if (tmpContextFl == null) {
            return false;
        }

        contextFL = tmpContextFl;
        screenFl = Screen.createInstance(contextFL);
        mouseFl = Mouse.createInstance(contextFL);

        mouseFl.dragSameCoord();

        logger.info("init created FL context is " + contextFL);

        return true;

    }

    private static Context searchFlContext() {
        Context tmpContextFl = GameWindowManager.findGameCoord(Constants.SCREEN_INDEX_PRINCIPAL, BotType.FL);

        if (tmpContextFl == null) {
            tmpContextFl = GameWindowManager.findGameCoord(Constants.SCREEN_INDEX_SECONDARY, BotType.FL);

            if (tmpContextFl != null) {
                return tmpContextFl.withScreenIndex(Constants.SCREEN_INDEX_SECONDARY);
            }
        }

        return tmpContextFl.withScreenIndex(Constants.SCREEN_INDEX_PRINCIPAL);
    }

    public static boolean checkFlContext() {
//        if (ImageReco.findFirst(screenFl.screenMemory(contextFL.origX(), 0, contextFL.width(), 50, false, "checkFL"),
//                Images.imgBluestackFL) == null) {
//            return false;
//        }

        return true;
    }

    public static boolean checkFlContextNeedRestart() {
        if (searchFlContext() == null) {
            return true;
        }

        return false;
    }

    public static void repositionningFlIfNeeded() {
        Context current = searchFlContext();
        if (current != null) {
            if (current.origX() != contextFL.origX()) {
                Mouse mouse = mouseScreen1;
                if (current.screenIndex() == Constants.SCREEN_INDEX_SECONDARY) {
                    mouse = mouseScreen2;
                }

                mouse.drag(current.origX() + 15, current.origY() - 10, contextFL.origX() + 15, current.origY() - 100,
                        current.screenIndex());
            }
        }
    }

    public static void resetRecoveryCount() {
        stats.setCountRecovery(0);
    }

    public static void resetStats() {
        logger.info("RESET STATS");
        stats.setCountAdmin(0);
        stats.setCountDev(0);
        stats.setCountFL(0);
        stats.setCountFLAdd(0);
        stats.setCountFLKick(0);
        stats.setCountFLTitle(0);
        stats.setCountInter(0);
        stats.setCountKickAdmin(0);
        stats.setCountKickDev(0);
        stats.setCountKickInter(0);
        stats.setCountKickMilitary(0);
        stats.setCountKickScience(0);
        stats.setCountKickSecu(0);
        stats.setCountKickStrat(0);
        stats.setCountMilitary(0);
        stats.setCountRecovery(0);
        stats.setCountScience(0);
        stats.setCountSecu(0);
        stats.setCountStrat(0);
        stats.setLastFlAction(new Date());
        stats.setFlTime(0);
        stats.setMoyenneFl(0);
        stats.setNextAdminKickCheck(new Date());
        stats.setNextDevKickCheck(new Date());
        stats.setNextInterKickCheck(new Date());
        stats.setNextMilitaryKickCheck(new Date());
        stats.setNextScienceKickCheck(new Date());
        stats.setNextSecuKickCheck(new Date());
        stats.setNextStratKickCheck(new Date());
        stats.setStart(0);
        stats.setStartDateStats(new Date());
        Frame.updateFrameStats(stats);
    }

}
