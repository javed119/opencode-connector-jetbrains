package com.epochbyte.actions;

import com.epochbyte.client.OpencodeClient;
import com.epochbyte.settings.OpencodeSettings;
import com.epochbyte.util.OpencodeReferenceBuilder;
import com.epochbyte.util.OpencodeTerminalUtil;
import com.epochbyte.util.ProjectUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class SendToOpencodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        String projectPath = ProjectUtils.getProjectPath(e);
        if (projectPath == null) {
            return;
        }
        
        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null) {
            Messages.showErrorDialog("Cannot determine file", "Error");
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String fileReference = OpencodeReferenceBuilder.buildEditorReference(
            projectPath,
            file,
            document,
            selectionModel
        );

        try {
            OpencodeClient client = new OpencodeClient(project);
            boolean focusTerminal = OpencodeSettings.getInstance().isFocusTerminalAfterSend();
            client.sendCode(fileReference).whenComplete((ignored, error) ->
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (error != null) {
                        Messages.showErrorDialog(
                            "Failed to send code: " + error.getMessage(),
                            "Error"
                        );
                        return;
                    }
                    if (focusTerminal) {
                        OpencodeTerminalUtil.focusOpenCodeTerminal(project);
                    }
                })
            );
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to send code: " + ex.getMessage(), 
                "Error"
            );
        }
    }

    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
    
    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
