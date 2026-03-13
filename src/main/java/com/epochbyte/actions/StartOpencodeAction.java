package com.epochbyte.actions;

import com.epochbyte.util.ProjectUtils;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
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
        String projectPath = ProjectUtils.getProjectPath(e);
        if (projectPath == null) {
            return;
        }
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        
        try {
            int port = findAvailablePort();
            
            TerminalToolWindowManager manager = TerminalToolWindowManager.getInstance(project);
            ShellTerminalWidget widget = manager.createLocalShellWidget(projectPath, "OpenCode");
            
            String command = "opencode --port " + port;
            widget.executeCommand(command);
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
    
    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
    
    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
