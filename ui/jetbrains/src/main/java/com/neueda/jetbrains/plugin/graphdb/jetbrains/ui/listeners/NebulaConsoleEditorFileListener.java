package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.listeners;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.plan.SQLConsoleToolbarForm;
import com.neueda.jetbrains.plugin.graphdb.platform.GraphConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/2 20:22
 */
public class NebulaConsoleEditorFileListener implements FileEditorManagerListener {

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!isSqlConsoleFile(source, file)) {
            return;
        }
        Project project = source.getProject();
        FileEditor fileEditor = source.getSelectedEditor(file);

        if (Objects.nonNull(fileEditor)) {
            // 追加顶部操作工具栏
            SQLConsoleToolbarForm toolbarForm = new SQLConsoleToolbarForm(project, fileEditor);
            source.addTopComponent(fileEditor, toolbarForm.getMainComponent());
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditorManagerListener.super.fileClosed(source, file);
    }

    private boolean isSqlConsoleFile(FileEditorManager source, VirtualFile file) {
        return file.getName().startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX);
    }
}
