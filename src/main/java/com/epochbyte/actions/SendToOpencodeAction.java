package com.epochbyte.actions;

import com.epochbyte.client.OpencodeClient;
import com.epochbyte.util.ProjectUtils;
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
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import java.util.Optional;

public class SendToOpencodeAction extends AnAction {
    private static final String OPEN_CODE_TAB_NAME = "OpenCode";
    
    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
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

        fileReference += " ";
        
        try {
            OpencodeClient client = new OpencodeClient(projectPath);
            client.sendCode(fileReference);
            focusOpenCodeTerminal(e.getProject());
        } catch (Exception ex) {
            Messages.showErrorDialog(
                "Failed to send code: " + ex.getMessage(), 
                "Error"
            );
        }
    }

    private void focusOpenCodeTerminal(Project project) {
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
