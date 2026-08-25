package com.epochbyte.client;

import com.epochbyte.util.OpencodeTerminalUtil;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OpencodeClient {
    private final Project project;
    
    public OpencodeClient(Project project) throws IOException {
        if (project == null || project.getBasePath() == null) {
            throw new IOException("Cannot determine IDEA project path");
        }
        this.project = project;
    }
    
    public CompletableFuture<Void> sendCode(String code) {
        return OpencodeTerminalUtil.sendPromptToOpenCodeTerminal(project, code);
    }
}
