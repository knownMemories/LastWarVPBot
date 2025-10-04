package com.km.lastwar.vpbot.utils;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ProcessManager {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(ProcessManager.class);
	private static String EMU_PROCESS_NAME = "dnconsole.exe";
	private static String BOT_FL_PROCESS = "C:\\LDPlayer\\LDPlayer9\\" + EMU_PROCESS_NAME + "\" launchex --index 0 --packagename com.fun.lastwar.gp";

	public static void startBotFL() {
		startProcess(BOT_FL_PROCESS);
	}

	public static void killBotFL() {
		killProcess(BOT_FL_PROCESS);
		killEmulator();
	}

	public static void restartBotFL() {
		logger.info("restartBotFL");
		ProcessManager.killBotFL();
		Utils.pause(5000);
		ProcessManager.startBotFL();
	}

	public static void killEmulator() {
		logger.info("Killing emulator");
		killProcess(EMU_PROCESS_NAME);
	}

	private static boolean killProcess(String processName) {
		String command = "taskkill /F /IM \"" + processName + "\"";

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
			Process process = processBuilder.start();

			process.waitFor();

			int exitCode = process.exitValue();
			if (exitCode == 0) {
				logger.info("process {} killed with success", processName);
				return true;
			} else {
				logger.info("Error to kill process {} exitCode {}", processName, exitCode);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void startProcess(String processName) {

		ProcessBuilder processBuilder = new ProcessBuilder(processName);

		try {
			Process process = processBuilder.start();
			while (!process.isAlive()) {
				logger.info("process {} not yet alive", processName);
				Utils.pause(500);
			}
			logger.info("process {} alive with pid {}", processName, process.pid());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}