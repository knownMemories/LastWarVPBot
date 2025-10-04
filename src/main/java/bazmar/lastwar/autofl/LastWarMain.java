package bazmar.lastwar.autofl;

import java.nio.file.Paths;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import bazmar.lastwar.autofl.data.BotType;
import bazmar.lastwar.autofl.data.Constants;
import bazmar.lastwar.autofl.data.Context;
import bazmar.lastwar.autofl.data.Coord;
import bazmar.lastwar.autofl.data.Images;
import bazmar.lastwar.autofl.data.Stats;
import bazmar.lastwar.autofl.data.Zone;
import bazmar.lastwar.autofl.data.Zones;
import bazmar.lastwar.autofl.exception.FlListNotFound;
import bazmar.lastwar.autofl.image.ImageReco;
import bazmar.lastwar.autofl.io.Frame;
import bazmar.lastwar.autofl.io.KeyScanner;
import bazmar.lastwar.autofl.io.Mouse;
import bazmar.lastwar.autofl.io.Screen;
import bazmar.lastwar.autofl.io.Serializer;
import bazmar.lastwar.autofl.thread.ScheduledLauncher;
import bazmar.lastwar.autofl.utils.FileManager;
import bazmar.lastwar.autofl.utils.ProcessManager;
import bazmar.lastwar.autofl.utils.Utils;
import ch.qos.logback.classic.Logger;

public class LastWarMain {

	private static Logger logger = (Logger) LoggerFactory.getLogger("App");

	private static int width = 1920, height = 1000;

	static Screen screenAll;

	static Mouse mouseScreen1;
	static Mouse mouseScreen2;
	static Mouse mouseFl;
	static Screen screenFl;
	public static Context contextFL;

	public static int PAUSE_BETWEEN_FL_ROUTINE = 1000;
	public static int PAUSE_BETWEEN_FL_ACTION = 1000;
	public static volatile boolean PAUSE = false;

	private static FirstLady fl;

	private static Stats stats = Serializer.loadStats();

	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Frame.createAndShowGUI();
			}
		});

		mouseScreen1 = Mouse.getInstance(Constants.DEFAULT_CONTEXT);
		mouseScreen2 = Mouse.getInstance(Constants.DEFAULT_CONTEXT_2);

		stats.setLastFlAction(new Date());
		init(false, stats);
		reloadContexts();

		@SuppressWarnings("unused")
		KeyScanner keyScanner = new KeyScanner();
		Utils.updateLogs();
		ScheduledLauncher scheduledLauncher = new ScheduledLauncher();
		scheduledLauncher.autoRestartBot(stats);
		Frame.updateLocation(contextFL.origX() + contextFL.width() + 40, 0, contextFL.screenIndex());

		firstLadyRoutine(stats);

	}

	private static void firstLadyRoutine(Stats stats) {
		while (1 > 0) {
			try {
				fl.firstLadySingleRoutine(stats);
			} catch (FlListNotFound e) {
				logger.error("firstLadySingleRoutine exception %s".formatted(e.getMessage()));
			}
			Utils.pause(PAUSE_BETWEEN_FL_ROUTINE);
			while (PAUSE) {
				Utils.pause(1000);
			}
		}
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

	private static void init(boolean launchBluestack, Stats stats) {
		// Remove existing handlers attached to the j.u.l root logger
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		// Add SLF4JBridgeHandler to j.u.l's root logger
		SLF4JBridgeHandler.install();

		logger.info("init default context is " + Constants.DEFAULT_CONTEXT);

		FileManager.createDirectoryIfNotExists(Constants.SANDBOX_PATH);
		FileManager.createDirectoryIfNotExists(Constants.MOUSE_SCREEN_PATH);
		FileManager.createDirectoryIfNotExists(Constants.DEBUG_SCREEN_PATH);
		FileManager.createDirectoryIfNotExists(Constants.KICK_SCREEN_PATH);

		FileManager.deleteDirectoryContents(Paths.get(Constants.DEBUG_SCREEN_PATH));
		FileManager.deleteDirectoryContents(Paths.get(Constants.MOUSE_SCREEN_PATH));
		FileManager.deleteDirectoryContents(Paths.get(Constants.TMP_SCREEN_PATH));

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
		Context tmpContextFl = findGameCoord(Constants.SCREEN_INDEX_PRINCIPAL, BotType.FL);
		if (tmpContextFl == null) {
			tmpContextFl = findGameCoord(Constants.SCREEN_INDEX_SECONDARY, BotType.FL);
			if (tmpContextFl != null) {
				tmpContextFl = tmpContextFl.withScreenIndex(Constants.SCREEN_INDEX_SECONDARY);
			}
		} else {
			tmpContextFl = tmpContextFl.withScreenIndex(Constants.SCREEN_INDEX_PRINCIPAL);
		}
		return tmpContextFl;
	}

	public static boolean checkFlContext() {
		if (ImageReco.findFirst(screenFl.screenMemory(contextFL.origX(), 0, contextFL.width(), 50, false, "checkFL"),
				Images.imgBluestackFL) == null) {
			return false;
		}
		return true;
	}

	private static Context findGameCoord(int screenIndex, BotType botType) {
		try {
			screenAll = Screen.createInstance(Constants.DEFAULT_CONTEXT);
			Coord origCoord = null;
			Zone topMenuZone = null;

			if (botType.equals(BotType.FL)) {
				topMenuZone = new Zone(0, 0, 1920, 50, screenIndex, "topMenuZoneBotFL");
				origCoord = ImageReco.findFirst(screenAll.screenMemory(topMenuZone, true), Images.imgBluestackFL, 10);
			}

			if (origCoord == null) {
				// should try another screen
				return null;
			}

			Zone bottomMenuZone = null;
			Coord endCoord = null;

			int maxAttempts = 10;
			int attempt = 0;

			while (endCoord == null && attempt < maxAttempts) {
				attempt++;

				if (botType.equals(BotType.FL)) {
					bottomMenuZone = new Zone(origCoord.getLeftBottomX(), 1080 - 100, 600, 100, screenIndex,
							"bottomMenuZoneFl");
				}

				endCoord = ImageReco.findFirst(screenAll.screenMemory(bottomMenuZone, true),
						Images.imgBluestackFullScreen, 20);

				if (endCoord == null) {
					logger.info("Try %s/%s: endCoord not found".formatted(attempt, maxAttempts));
					Utils.pause(LastWarMain.PAUSE_BETWEEN_FL_ACTION);
				}
			}

			if (endCoord == null) {
				logger.error("RECOVERY endCoord not found after %s try Reboot and wait 60s".formatted(attempt));
				ProcessManager.restartBotFL();
				stats.incrementCountRecovery();
				Utils.pause(60000);
				return null;
			}

			endCoord = Zones.putInGameXYValues(endCoord, bottomMenuZone);

			width = endCoord.getLeftBottomX() - origCoord.getLeftBottomX();
			height = endCoord.getLeftBottomY() - origCoord.getLeftBottomY();

			return new Context(origCoord.getLeftBottomX(), origCoord.getLeftBottomY(), width, height, screenIndex,
					botType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

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
