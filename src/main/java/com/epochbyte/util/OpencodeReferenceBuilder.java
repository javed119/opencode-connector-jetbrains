package com.epochbyte.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class OpencodeReferenceBuilder {

    private OpencodeReferenceBuilder() {
    }

    public static String buildEditorReference(
        String projectPath,
        VirtualFile file,
        Document document,
        SelectionModel selectionModel
    ) {
        String relativePath = toRelativePath(projectPath, file);
        if (!selectionModel.hasSelection()) {
            return "@" + relativePath + " ";
        }

        int startLine = document.getLineNumber(selectionModel.getSelectionStart()) + 1;
        int endLine = document.getLineNumber(selectionModel.getSelectionEnd()) + 1;

        if (startLine == endLine) {
            return "@" + relativePath + "#L" + startLine + " ";
        }

        return "@" + relativePath + "#L" + startLine + "-" + endLine + " ";
    }

    public static String buildFileReferences(String projectPath, VirtualFile[] files) {
        List<String> relativePaths = collectRelativePaths(projectPath, files);
        if (relativePaths.isEmpty()) {
            return "";
        }

        List<String> references = new ArrayList<>();
        for (String relativePath : relativePaths) {
            references.add("@" + relativePath);
        }
        return String.join(" ", references) + " ";
    }

    public static List<String> collectRelativePaths(String projectPath, VirtualFile[] files) {
        Path project = Paths.get(projectPath).normalize();

        List<String> relativePaths = new ArrayList<>();
        for (VirtualFile file : files) {
            if (file == null || !file.isValid() || !file.isInLocalFileSystem()) {
                continue;
            }

            Path target = Paths.get(file.getPath()).normalize();
            if (!target.startsWith(project)) {
                continue;
            }

            String relativePath = project.relativize(target).toString().replace('\\', '/');
            if (!relativePath.isEmpty()) {
                relativePaths.add(relativePath);
            }
        }

        return normalizeRelativePaths(relativePaths);
    }

    static List<String> normalizeRelativePaths(List<String> relativePaths) {
        return relativePaths.stream()
            .distinct()
            .sorted(Comparator.naturalOrder())
            .filter(path -> isTopLevelSelection(path, relativePaths))
            .collect(Collectors.toList());
    }

    private static boolean isTopLevelSelection(String path, List<String> allPaths) {
        for (String candidate : allPaths) {
            String prefix = candidate + "/";
            if (!candidate.equals(path) && path.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }

    private static String toRelativePath(String projectPath, VirtualFile file) {
        return toRelativePath(projectPath, file.getPath());
    }

    static String toRelativePath(String projectPath, String targetPath) {
        Path project = Paths.get(projectPath).normalize();
        Path target = Paths.get(targetPath).normalize();
        return project.relativize(target).toString().replace('\\', '/');
    }
}
