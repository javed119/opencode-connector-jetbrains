package com.epochbyte.util;

import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessScanner {
    private static final Logger LOG = Logger.getInstance(ProcessScanner.class);
    private static final String PROCESS_NAME = "opencode";
    private static final Pattern PORT_PATTERN = Pattern.compile("--port[=\\s]+(\\d+)");

    public static List<Integer> findOpencodePorts() {
        String os = System.getProperty("os.name").toLowerCase();
        List<String> commandLines;

        if (os.contains("win")) {
            commandLines = scanWindowsProcesses();
        } else {
            commandLines = scanUnixProcesses();
        }

        return extractPorts(commandLines);
    }

    private static List<String> scanUnixProcesses() {
        return executeCommand("sh", "-c", "ps aux | grep " + PROCESS_NAME + " | grep -v grep");
    }

    static List<String> scanWindowsProcesses() {
        return executeCommand(
            "powershell",
            "-NoProfile",
            "-NonInteractive",
            "-Command",
            "Get-CimInstance Win32_Process | Where-Object {$_.Name -like '*" + PROCESS_NAME + "*'} | Select-Object -ExpandProperty CommandLine"
        );
    }

    static boolean isTargetProcess(String processName, String commandLine) {
        if (processName == null || commandLine == null || commandLine.isEmpty()) {
            return false;
        }

        return processName.toLowerCase(Locale.ROOT).startsWith(PROCESS_NAME)
            || commandLine.toLowerCase(Locale.ROOT).contains(PROCESS_NAME);
    }

    private static List<String> executeCommand(String... command) {
        List<String> result = new ArrayList<>();
        Process process = null;
        try {
            process = new ProcessBuilder(command).start();

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            ); BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        result.add(line);
                    }
                }

                StringBuilder errors = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errors.append(line).append("\n");
                }

                process.waitFor();
                if (!errors.isEmpty()) {
                    LOG.warn("Process execution errors: " + errors);
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to scan processes: " + e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    static List<Integer> extractPorts(List<String> commandLines) {
        List<Integer> ports = new ArrayList<>();
        for (String line : commandLines) {
            Matcher matcher = PORT_PATTERN.matcher(line);
            while (matcher.find()) {
                try {
                    int port = Integer.parseInt(matcher.group(1));
                    if (port > 0 && port <= 65535 && !ports.contains(port)) {
                        ports.add(port);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return ports;
    }
}
