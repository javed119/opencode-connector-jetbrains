package com.epochbyte.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PortDetector {
    private static final int HTTP_TIMEOUT_MS = 3000;
    private static final Gson GSON = new Gson();
    private static final Logger LOG = Logger.getInstance(PortDetector.class);
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static final Pattern WINDOWS_DRIVE_PATH = Pattern.compile("^[A-Za-z]:[\\\\/].*");

    public static int detectPort(String host, String projectPath) throws IOException {
        List<Integer> ports = ProcessScanner.findOpencodePorts();
        if (ports.isEmpty()) {
            throw new IOException("No OpenCode process found");
        }

        for (int port : ports) {
            if (matchesProject(host, port, projectPath)) {
                return port;
            }
        }

        throw new IOException("No OpenCode instance found for project: " + projectPath);
    }

    static boolean samePath(String projectPath, String remotePath) {
        String normalizedProject = normalizePath(projectPath);
        String normalizedRemote = normalizePath(remotePath);
        if (IS_WINDOWS || isWindowsStylePath(normalizedProject) || isWindowsStylePath(normalizedRemote)) {
            return normalizedProject.equalsIgnoreCase(normalizedRemote);
        }
        return normalizedProject.equals(normalizedRemote);
    }

    private static String normalizePath(String path) {
        String normalizedPath = path.replace('\\', '/');
        while (normalizedPath.contains("//")) {
            normalizedPath = normalizedPath.replace("//", "/");
        }
        if (normalizedPath.endsWith("/") && normalizedPath.length() > 1) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        if (isWindowsStylePath(normalizedPath)) {
            return normalizedPath.toLowerCase(Locale.ROOT);
        }
        return normalizedPath;
    }

    private static boolean isWindowsStylePath(String path) {
        return WINDOWS_DRIVE_PATH.matcher(path).matches();
    }

    private static boolean matchesProject(String host, int port, String projectPath) {
        HttpURLConnection conn = null;
        try {
            URL url = toUrl(host + ":" + port + "/path");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(HTTP_TIMEOUT_MS);
            conn.setReadTimeout(HTTP_TIMEOUT_MS);

            if (conn.getResponseCode() != 200) {
                return false;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject project = GSON.fromJson(response.toString(), JsonObject.class);
            String remotePath = project.get("directory").getAsString();

            LOG.info("Remote Path: " + remotePath);
            LOG.info("Project Path: " + projectPath);

            return samePath(projectPath, remotePath);
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static URL toUrl(String endpoint) throws IOException {
        try {
            return URI.create(endpoint).toURL();
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid OpenCode endpoint: " + endpoint, e);
        }
    }
}
