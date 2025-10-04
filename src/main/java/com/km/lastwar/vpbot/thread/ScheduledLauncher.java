package com.km.lastwar.vpbot.thread;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.km.lastwar.vpbot.LastWarMain;
import com.km.lastwar.vpbot.data.Stats;
import com.km.lastwar.vpbot.utils.ProcessManager;
import com.km.lastwar.vpbot.utils.Utils;
import org.slf4j.LoggerFactory;

import com.km.lastwar.vpbot.io.Serializer;
import ch.qos.logback.classic.Logger;

public class ScheduledLauncher {

	private static Logger logger = (Logger) LoggerFactory.getLogger("ScheduledLauncher");

	private ScheduledExecutorService executor;

	public void autoRestartBot(Stats stats) {
		executor = Executors.newScheduledThreadPool(10);
		Runnable task = () -> {
			logger.info("autoRestartBot lastFLAction %s".formatted(Utils.humanReadableDate(stats.getLastFlAction())));

			Serializer.saveStats(stats);

			// Avoid notif bug and PAUSE forget by mistake
			if (stats.getLastFlAction().before(Utils.getPastMinute(10))) {
				logger.info("autoRestartBot FL doing nothing last 10 minutes Reboot Android Emu in case of");
//				ProcessManager.restartBotFL();
				stats.incrementCountRecovery();
				stats.setLastFlAction(new Date());
				LastWarMain.PAUSE = false;
				return;
			}

			if (LastWarMain.PAUSE) {
				logger.info("autoRestartBot bypass (PAUSE)");
				return;
			}

			if (!LastWarMain.checkFlContext()) {
				logger.info("Start process FL (checkFlContext KO)");
				ProcessManager.startBotFL();
				Utils.pause(10000);
				if (LastWarMain.checkFlContextNeedRestart()) {
					logger.info("Restart process FL (checkFlContextNeedRestart true)");
//					ProcessManager.restartBotFL();
					stats.incrementCountRecovery();
					return;
				}
				LastWarMain.repositionningFlIfNeeded();
			}
		};

		long initialDelay = 0;
		long period = 60;
		TimeUnit timeUnit = TimeUnit.SECONDS;

		executor.scheduleAtFixedRate(task, initialDelay, period, timeUnit);
	}
}