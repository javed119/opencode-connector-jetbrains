package com.epochbyte.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessScanner {
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
    
    private static List<String> scanWindowsProcesses() {
        return executeCommand("cmd", "/c", "wmic process where \"commandline like '%" + PROCESS_NAME + "%'\" get commandline");
    }
    
    private static List<String> executeCommand(String... command) {
        List<String> result = new ArrayList<>();
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line);
                }
            }
            reader.close();
        } catch (Exception e) {
        }
        return result;
    }
    
    private static List<Integer> extractPorts(List<String> commandLines) {
        List<Integer> ports = new ArrayList<>();
        for (String line : commandLines) {
            Matcher matcher = PORT_PATTERN.matcher(line);
            if (matcher.find()) {
                try {
                    ports.add(Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return ports;
    }
}
