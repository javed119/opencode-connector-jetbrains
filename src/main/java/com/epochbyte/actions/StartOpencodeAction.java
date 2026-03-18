package com.epochbyte.actions;

import com.epochbyte.util.OpencodePortRange;
import com.epochbyte.util.ProjectUtils;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import java.net.ServerSocket;
import java.util.Random;

public class StartOpencodeAction extends AnAction {

    private static final int MAX_ATTEMPTS = 100;
    private static final Random random = new Random();
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
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
            TerminalWidget widget = manager.createShellWidget(projectPath, "OpenCode", true, false);
            
            String command = "opencode --port " + port;
            widget.sendCommandToExecute(command);
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to start OpenCode: " + ex.getMessage(), 
                "Error"
            );
        }
    }
    
    private int findAvailablePort() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int port = OpencodePortRange.PORT_START
                + random.nextInt(OpencodePortRange.PORT_END - OpencodePortRange.PORT_START + 1);
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
