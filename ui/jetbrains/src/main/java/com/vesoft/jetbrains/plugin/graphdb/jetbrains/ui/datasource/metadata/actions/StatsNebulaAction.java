package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSchema;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.analytics.Analytics;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.ShowNebulaDDLDialog;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.FileUtil;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.Notifier;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 14:52
 */
public class StatsNebulaAction extends AnAction {

    private NebulaSchema schema;
    private DataSourceApi dataSourceApi;

    public StatsNebulaAction(NebulaSchema data, DataSourceApi dataSourceApi) {
        super("Submit Stats", "submit stats", GraphIcons.Nodes.STATISTIC);
        this.schema = data;
        this.dataSourceApi = dataSourceApi;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        String space = schema.getSpaceName();
        String nGql = String.format("USE %s;SUBMIT JOB STATS;", space);
        Analytics.event(dataSourceApi, "openEditor");
        try {
            FileUtil.openFile(project, FileUtil.getDataSourceFile(project, dataSourceApi));
            project.getMessageBus().syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC)
                    .executeQuery(dataSourceApi, new ExecuteQueryPayload(nGql));
        } catch (IOException exception) {
            Notifier.error("Open editor error", exception.getMessage());
        }
    }
}
