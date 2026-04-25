package com.epochbyte.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

public class ProjectUtils {
    
    @Nullable
    public static String getProjectPath(AnActionEvent e) {
        String projectPath = getProjectPathQuietly(e);
        if (projectPath != null) {
            return projectPath;
        }

        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project found", "Error");
            return null;
        }

        Messages.showErrorDialog("Cannot determine project path", "Error");
        return null;
    }

    @Nullable
    public static String getProjectPathQuietly(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return null;
        }

        String projectPath = project.getBasePath();
        if (projectPath == null) {
            return null;
        }

        return projectPath;
    }
}
