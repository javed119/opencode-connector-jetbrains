package com.epochbyte.actions;

import com.epochbyte.client.OpencodeClient;
import com.epochbyte.settings.OpencodeSettings;
import com.epochbyte.util.OpencodeReferenceBuilder;
import com.epochbyte.util.OpencodeTerminalUtil;
import com.epochbyte.util.ProjectUtils;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class AddToOpencodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String projectPath = ProjectUtils.getProjectPath(e);
        if (projectPath == null) {
            return;
        }

        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (files == null || files.length == 0) {
            Messages.showErrorDialog("No files or directories selected", "Error");
            return;
        }

        String fileReferences = OpencodeReferenceBuilder.buildFileReferences(projectPath, files);
        if (fileReferences.isEmpty()) {
            Messages.showErrorDialog("No valid project files or directories selected", "Error");
            return;
        }

        try {
            OpencodeClient client = new OpencodeClient(projectPath);
            client.sendCode(fileReferences);
            if (OpencodeSettings.getInstance().isFocusTerminalAfterSend()) {
                OpencodeTerminalUtil.focusOpenCodeTerminal(e.getProject());
            }
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to add files to OpenCode: " + ex.getMessage(),
                "Error"
            );
        }
    }

    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        String projectPath = ProjectUtils.getProjectPathQuietly(e);
        boolean visible = e.getProject() != null
            && projectPath != null
            && files != null
            && !OpencodeReferenceBuilder.collectRelativePaths(projectPath, files).isEmpty();
        e.getPresentation().setEnabledAndVisible(visible);
    }
}
