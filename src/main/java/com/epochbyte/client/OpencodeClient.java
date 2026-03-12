package com.epochbyte.client;

import com.epochbyte.settings.OpencodeSettings;
import com.epochbyte.util.PortDetector;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpencodeClient {
    private final String baseUrl;
    private final String sessionId;
    private final Gson gson;
    
    public OpencodeClient(String projectPath) throws IOException {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        int port = PortDetector.detectPort(settings.host, projectPath);
        this.baseUrl = settings.host + ":" + port;
        this.sessionId = settings.sessionId;
        this.gson = new Gson();
    }
    
    public void sendCode(String code) throws IOException {
        String endpoint = baseUrl + "/tui/append-prompt";
        
        Map<String, Object> body = new HashMap<>();
        body.put("text", code);
        
        String jsonBody = gson.toJson(body);
        
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }
}
