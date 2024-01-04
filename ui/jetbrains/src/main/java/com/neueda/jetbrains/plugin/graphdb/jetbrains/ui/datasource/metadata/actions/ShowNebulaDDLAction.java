package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.ShowNebulaDDLDialog;
import icons.GraphIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 14:52
 */
public class ShowNebulaDDLAction extends AnAction {

    private Object data;

    public ShowNebulaDDLAction(Object data) {
        super("show ddl", "show ddl", GraphIcons.scaleToWidth(GraphIcons.Nodes.DDL, 16));
        this.data = data;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new ShowNebulaDDLDialog(e.getProject(), data).setVisible(true);
    }
}
