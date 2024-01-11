package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphMetadata;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.query.NebulaGraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourcesComponent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.database.DatabaseManagerService;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.QueryExecutionProcessEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.NameUtil;
import com.vesoft.jetbrains.plugin.graphdb.platform.GraphConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/3 16:07
 */
public class SQLConsoleSpaceSelectAction extends ComboBoxAction implements DumbAware {

    public static final String ID = "NebulaGraph.Actions.CurrentSpace";

    private DataSourceApi dataSourceApi;

    private Project project;

    private QueryExecutionProcessEvent queryExecutionProcessEvent;

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        getTemplatePresentation().setText("Space Loading...", false);
        if (Objects.isNull(dataSourceApi)) {
            getTemplatePresentation().setText("--No space--", false);
            return actionGroup;
        }
        DataSourceMetadata metadata = getDataSourceMetadata();
        if (metadata == null || !(metadata instanceof NebulaDataSourceMetadata)) {
            getTemplatePresentation().setText("--No space--", false);
            return actionGroup;
        }
        List<NebulaSpace> nebulaSpaceList = ((NebulaDataSourceMetadata) metadata).getNebulaGraphMetadata().getNebulaSpaceList();
        if (nebulaSpaceList == null || nebulaSpaceList.isEmpty()) {
            return actionGroup;
        }
        for (NebulaSpace nebulaSpace : nebulaSpaceList) {
            actionGroup.add(new AnAction(formatName(nebulaSpace.getSpaceName())) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    switchSpace(e, nebulaSpace.getSpaceName());
                }
            });
        }
        getTemplatePresentation().setText(((NebulaDataSourceMetadata) metadata).getNebulaGraphMetadata().getCurrentSpace(), false);
        return actionGroup;
    }

    private String formatName(String name) {
        return "<html><u>" + name + "</u></html>";
    }

    private DataSourceMetadata getDataSourceMetadata() {
        DataSourceMetadata metadata = DataContext.getInstance(this.project).getMetadata(dataSourceApi);
        if (metadata == null) {
            DatabaseManagerService databaseManagerService = ServiceManager.getService(DatabaseManagerService.class);
            GraphDatabaseApi database = databaseManagerService.getDatabaseFor(dataSourceApi);
            NebulaGraphMetadata graphMetadata = (NebulaGraphMetadata) database.metadata();
            metadata = new NebulaDataSourceMetadata(graphMetadata);
            DataContext.getInstance(this.project).addMetadata(dataSourceApi, metadata);
        }
        return metadata;
    }

    private void switchSpace(AnActionEvent e, String space) {
        MessageBus messageBus = project.getMessageBus();
        messageBus.syncPublisher(CommonConsoleLogEvent.COMMON_LOG_EVENT_TOPIC)
                .info("switch space to : " + space);

        ExecuteQueryEvent executeQueryEvent = messageBus.syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC);
        executeQueryEvent.executeQuery(dataSourceApi, new ExecuteQueryPayload("USE " + space));
        DataContext.getInstance(project).setCurrentSpace(dataSourceApi, space);
    }

    @Override
    public void update(AnActionEvent e) {
        this.project = e.getProject();
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (nonNull(virtualFile)) {
            String fileName = virtualFile.getName();
            if (fileName.startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX)) {
                Optional<? extends DataSourceApi> boundDataSource = e.getProject()
                        .getComponent(DataSourcesComponent.class)
                        .getDataSourceContainer()
                        .findDataSource(NameUtil.extractDataSourceUUID(fileName));
                if (boundDataSource.isPresent()) {
                    dataSourceApi = boundDataSource.get();
                }
            }
        }
//        String selectSpaceName = DataContext.getInstance(project).getCurrentSpace(virtualFile.getName());
//        if (selectSpaceName != null) {
//            setCurrentSpace(e, selectSpaceName);
//        }
        if (queryExecutionProcessEvent == null) {
            queryExecutionProcessEvent = new QueryExecutionProcessEvent() {
                @Override
                public void resultReceived(ExecuteQueryPayload payload, GraphQueryResult result) {
                    if (result instanceof NebulaGraphQueryResult && payload.getFileName().equals(virtualFile.getName())) {
                        setCurrentSpace(e, ((NebulaGraphQueryResult) result).getCurrentSpace());
                    }
                }
            };
            this.project.getMessageBus().connect().subscribe(QueryExecutionProcessEvent.QUERY_EXECUTION_PROCESS_TOPIC, queryExecutionProcessEvent);
            String currentSpace = DataContext.getInstance(project).getCurrentSpace(dataSourceApi);
            setCurrentSpace(e, currentSpace);
        }
    }

    private void setCurrentSpace(AnActionEvent e, String spaceName) {
        Presentation presentation = e.getPresentation();
        presentation.setText(spaceName, false);
        presentation.setEnabled(true);
        presentation.setVisible(true);
        presentation.setDescription("Current Space");
        DataContext.getInstance(project).setCurrentSpace(dataSourceApi, spaceName);
    }


}
