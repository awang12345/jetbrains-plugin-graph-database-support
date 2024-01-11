package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSchema;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.ShowNebulaDDLDialog;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 14:52
 */
public class ShowNebulaDDLAction extends AnAction {

    private NebulaSchema schema;

    public ShowNebulaDDLAction(NebulaSchema data) {
        super("show ddl", "show ddl", GraphIcons.scaleToWidth(GraphIcons.Nodes.DDL, 16));
        this.schema = data;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new ShowNebulaDDLDialog(e.getProject(), schema).setVisible(true);
    }
}
