package com.km.lastwar.vpbot.infrastructure.process;

import com.km.lastwar.vpbot.constants.GameProcess;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
public class ProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);

    public void startVPProcess() {

        executeCommand(GameProcess.BOT_VP_PROCESS);
    }

    public void killVPProcess() {

        killProcess(GameProcess.EMU_PROCESS_NAME);
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