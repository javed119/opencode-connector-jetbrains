package com.epochbyte.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PortDetector {
    private static final int PORT_START = 20000;
    private static final int PORT_END = 40000;
    private static final int TIMEOUT_MS = 500;
    private static final Gson gson = new Gson();
    
    public static int detectPort(String host, String projectPath) throws IOException {
        for (int port = PORT_START; port <= PORT_END; port++) {
            if (matchesProject(host, port, projectPath)) {
                return port;
            }
        }
        throw new IOException("No OpenCode instance found for project: " + projectPath);
    }
    
    private static boolean matchesProject(String host, int port, String projectPath) {
        try {
            URL url = new URL(host + ":" + port + "/path");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            if (conn.getResponseCode() != 200) {
                return false;
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();
            
            JsonObject project = gson.fromJson(response.toString(), JsonObject.class);
            String remotePath = project.get("directory").getAsString();
            
            return projectPath.equals(remotePath);
        } catch (Exception e) {
            return false;
        }
    }
}
