package com.epochbyte.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import java.net.ServerSocket;
import java.util.Random;

public class StartOpencodeAction extends AnAction {
    
    private static final int PORT_START = 20000;
    private static final int PORT_END = 40000;
    private static final int MAX_ATTEMPTS = 100;
    private static final Random random = new Random();
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project found", "Error");
            return;
        }
        
        String projectPath = project.getBasePath();
        if (projectPath == null) {
            Messages.showErrorDialog("Cannot determine project path", "Error");
            return;
        }
        
        try {
            int port = findAvailablePort();
            
            TerminalToolWindowManager manager = TerminalToolWindowManager.getInstance(project);
            ShellTerminalWidget widget = manager.createLocalShellWidget(projectPath, "OpenCode");
            
            String command = "opencode --port " + port;
            widget.executeCommand(command);
            
            ToolWindow terminalWindow = ToolWindowManager.getInstance(project)
                .getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
            if (terminalWindow != null) {
                terminalWindow.activate(null);
            }
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to start OpenCode: " + ex.getMessage(), 
                "Error"
            );
        }
    }
    
    private int findAvailablePort() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int port = PORT_START + random.nextInt(PORT_END - PORT_START + 1);
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (Exception ignored) {
            }
        }
        throw new Exception("No available port found after " + MAX_ATTEMPTS + " attempts");
    }
    
    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
