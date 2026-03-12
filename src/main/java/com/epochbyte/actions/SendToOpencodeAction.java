package com.epochbyte.actions;

import com.epochbyte.client.OpencodeClient;
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

public class SendToOpencodeAction extends AnAction {
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        
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
        
        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null) {
            Messages.showErrorDialog("Cannot determine file", "Error");
            return;
        }
        
        SelectionModel selectionModel = editor.getSelectionModel();
        if (!selectionModel.hasSelection()) {
            Messages.showWarningDialog("Please select code first", "No Selection");
            return;
        }
        
        int startLine = document.getLineNumber(selectionModel.getSelectionStart()) + 1;
        int endLine = document.getLineNumber(selectionModel.getSelectionEnd()) + 1;
        
        String relativePath = file.getPath().replace(projectPath + "/", "");
        String fileReference;
        if (startLine == endLine) {
            fileReference = "@" + relativePath + "#L" + startLine;
        } else {
            fileReference = "@" + relativePath + "#L" + startLine + "-" + endLine;
        }
        
        try {
            OpencodeClient client = new OpencodeClient(projectPath);
            client.sendCode(fileReference);
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to send code: " + ex.getMessage(), 
                "Error"
            );
        }
    }
    
    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
