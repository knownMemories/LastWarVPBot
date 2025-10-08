package com.km.lastwar.vpbot.infrastructure;

import com.km.lastwar.vpbot.constants.GameProcess;
import com.km.lastwar.vpbot.infrastructure.util.ThreadManipulator;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdbCommandExecutor {

    public String execute(String... arguments) {
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(arguments));
        processBuilder.redirectErrorStream(true);

        ThreadManipulator.pause(5000);

        try {
            Process process = processBuilder.start();

            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new RuntimeException("ADB command timed out after 10 seconds: " + String.join(" ", buildCommand(arguments)));
            }

            int exitCode = process.exitValue();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String output = reader.lines().collect(Collectors.joining("\n"));

                if (exitCode != 0) {
                    throw new RuntimeException(
                            String.format("ADB command failed with exit code %d. Command: %s. Output: %s",
                                          exitCode,
                                          String.join(" ", buildCommand(arguments)),
                                          output)
                    );
                }

                return output;
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to execute ADB command: " + String.join(" ", buildCommand(arguments)), e);
        }
    }

    private String[] buildCommand(String... arguments) {
        String[] command = new String[arguments.length + 1];
        command[0] = GameProcess.ADB_EXE_PATH;
        System.arraycopy(arguments, 0, command, 1, arguments.length);
        return command;
    }
}