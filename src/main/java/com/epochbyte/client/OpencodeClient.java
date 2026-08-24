package com.epochbyte.client;

import com.epochbyte.settings.OpencodeSettings;
import com.epochbyte.util.PortDetector;
import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OpencodeClient {
    private static final int TIMEOUT_MS = 3000;
    
    private final String baseUrl;
    private final String projectPath;
    private final Gson gson;

    private static final Logger LOG = Logger.getInstance(OpencodeClient.class);
    
    public OpencodeClient(Project project) throws IOException {
        if (project == null || project.getBasePath() == null) {
            throw new IOException("Cannot determine IDEA project path");
        }

        String projectPath = project.getBasePath();
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        if (settings == null) {
            throw new IOException("Failed to load OpenCode settings");
        }
        String configuredServerUrl = OpencodeSettings.getInstance().getServerUrl();
        if (configuredServerUrl.isBlank()) {
            int port = PortDetector.detectPort(settings.host, projectPath);
            this.baseUrl = settings.host + ":" + port;
            LOG.info("Opencode server started on port " + port);
        } else {
            try {
                this.baseUrl = OpencodeSettings.normalizeServerUrl(configuredServerUrl);
            } catch (IllegalArgumentException ex) {
                throw new IOException("Invalid OpenCode server URL", ex);
            }
            LOG.info("Using configured OpenCode server " + this.baseUrl);
        }
        this.projectPath = projectPath;
        this.gson = new Gson();
    }
    
    public void sendCode(String code) throws IOException {
        String endpoint = baseUrl + "/tui/append-prompt";
        
        Map<String, Object> body = new HashMap<>();
        body.put("text", code);
        
        String jsonBody = gson.toJson(body);
        HttpURLConnection conn = createConnection(endpoint, "POST");
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }
    
    private HttpURLConnection createConnection(String endpoint, String method) throws IOException {
        return createConnection(endpoint, method, projectPath);
    }

    private static HttpURLConnection createConnection(
        String endpoint,
        String method,
        String projectPath
    ) throws IOException {
        URL url = toUrl(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        if (projectPath != null) {
            conn.setRequestProperty("x-opencode-directory", projectPath);
        }
        conn.setDoOutput(true);
        return conn;
    }

    private static URL toUrl(String endpoint) throws IOException {
        try {
            return URI.create(endpoint).toURL();
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid OpenCode endpoint: " + endpoint, e);
        }
    }
}
