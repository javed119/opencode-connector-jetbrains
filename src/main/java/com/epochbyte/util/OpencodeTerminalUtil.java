package com.epochbyte.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import java.util.Optional;

public final class OpencodeTerminalUtil {
    private static final String OPEN_CODE_TAB_NAME = "OpenCode";

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

        Optional<TerminalWidget> widgetToFocus = manager.getTerminalWidgets().stream()
            .filter(widget -> widget.getTerminalTitle().buildTitle().contains(OPEN_CODE_TAB_NAME))
            .findFirst();

        if (widgetToFocus.isEmpty()) {
            toolWindow.activate(null);
            return;
        }

        toolWindow.activate(() -> widgetToFocus.get().requestFocus());
    }
}
