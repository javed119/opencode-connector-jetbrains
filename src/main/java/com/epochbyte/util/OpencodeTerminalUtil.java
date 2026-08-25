package com.epochbyte.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OpencodeTerminalUtil {
    private static final String OPEN_CODE_TAB_NAME = "OpenCode";
    private static final String OPEN_CODE_SESSION_TITLE_PREFIX = "OC | ";
    private static final String BRACKETED_PASTE_START = "\u001B[200~";
    private static final String BRACKETED_PASTE_END = "\u001B[201~";

    private OpencodeTerminalUtil() {
    }

    public static void focusOpenCodeTerminal(Project project) {
        if (project == null) {
            return;
        }

        TerminalToolWindowManager manager = TerminalToolWindowManager.getInstance(project);
        ToolWindow toolWindow = manager.getToolWindow();
        if (toolWindow == null) {
            return;
        }

        Optional<TerminalWidget> widgetToFocus = findOpenCodeTerminal(manager);

        if (widgetToFocus.isEmpty()) {
            toolWindow.activate(null);
            return;
        }

        toolWindow.activate(() -> widgetToFocus.get().requestFocus());
    }

    public static CompletableFuture<Void> sendPromptToOpenCodeTerminal(Project project, String prompt) {
        if (project == null) {
            return CompletableFuture.failedFuture(new IOException("No IDEA project found"));
        }

        TerminalToolWindowManager manager = TerminalToolWindowManager.getInstance(project);
        Optional<TerminalWidget> widget = findOpenCodeTerminal(manager);
        if (widget.isEmpty()) {
            return CompletableFuture.failedFuture(new IOException("OpenCode terminal not found"));
        }

        CompletableFuture<Void> result = new CompletableFuture<>();
        try {
            widget.get().getTtyConnectorAccessor().executeWithTtyConnector(connector ->
                CompletableFuture.runAsync(() -> {
                    try {
                        // Write to this project's TTY so the TUI's current session handles the prompt.
                        connector.write(BRACKETED_PASTE_START + prompt + BRACKETED_PASTE_END);
                        result.complete(null);
                    } catch (IOException ex) {
                        result.completeExceptionally(ex);
                    }
                })
            );
        } catch (RuntimeException ex) {
            result.completeExceptionally(ex);
        }
        return result.orTimeout(3, TimeUnit.SECONDS);
    }

    private static Optional<TerminalWidget> findOpenCodeTerminal(TerminalToolWindowManager manager) {
        return manager.getTerminalWidgets().stream()
            .filter(OpencodeTerminalUtil::isOpenCodeTerminal)
            .findFirst();
    }

    private static boolean isOpenCodeTerminal(TerminalWidget widget) {
        String title = widget.getTerminalTitle().buildTitle();
        return OPEN_CODE_TAB_NAME.equals(title) || title.startsWith(OPEN_CODE_SESSION_TITLE_PREFIX);
    }
}
