package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSchema;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import icons.GraphIcons;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 业务说明：预览数据
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/11 14:47
 */
public class PreviewNebulaDataAction extends AnAction {

    private NebulaSchema nebulaSchema;

    private DataSourceApi dataSourceApi;

    public PreviewNebulaDataAction(NebulaSchema nebulaSchema, DataSourceApi dataSourceApi) {
        super("Preview Data", "preview data", GraphIcons.Nodes.PREVIEW);
        this.nebulaSchema = nebulaSchema;
        this.dataSourceApi = dataSourceApi;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String sql = null;
        String space = nebulaSchema.getSpaceName();
        if (nebulaSchema instanceof NebulaTag) {
            sql = String.format("USE %s; MATCH (v:%s) RETURN v LIMIT 10", space, ((NebulaTag) nebulaSchema).getTagName());
        } else if (nebulaSchema instanceof NebulaEdge) {
            sql = String.format("USE %s; MATCH (a)-[e:%s]->(b) RETURN a,b,e LIMIT 10", space, ((NebulaEdge) nebulaSchema).getEdgeName());
        } else if (nebulaSchema instanceof NebulaSpace) {
            sql = String.format("USE %s; MATCH (a)-[e]->(b) RETURN a,b,e LIMIT 10", space);
        }
        if (sql != null) {
            ExecuteQueryEvent executeQueryEvent = e.getProject().getMessageBus().syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC);
            executeQueryEvent.executeQuery(dataSourceApi, new ExecuteQueryPayload(sql));
        }
    }
}
