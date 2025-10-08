package com.km.lastwar.vpbot.infrastructure.process;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
public class ProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);

    private static final String EMU_PROCESS_NAME = "dnconsole.exe";

    private static final String BOT_VP_PROCESS = "C:\\LDPlayer\\LDPlayer9\\dnconsole.exe launchex --index 0 --packagename com.fun.lastwar.gp";

    public void startVPProcess() {

        executeCommand(BOT_VP_PROCESS);
    }

    public void killVPProcess() {

        killProcess(EMU_PROCESS_NAME);
    }

    private void killProcess(String processName) {

        String command = "taskkill /F /IM \"" + processName + "\"";
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (process.exitValue() == 0) {
                logger.info("Process {} killed successfully", processName);
            } else {
                logger.warn("Failed to kill process {}: exit code {}", processName, process.exitValue());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error killing process {}", processName, e);
        }
    }

    private void executeCommand(String command) {

        try {
            Process process = Runtime.getRuntime().exec(command);
            logger.info("Started process with PID: {}", process.pid());
        } catch (IOException e) {
            logger.error("Failed to start process: {}", command, e);
        }
    }
}