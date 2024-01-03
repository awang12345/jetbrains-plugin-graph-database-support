package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.diagnostic.PerformanceWatcher;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.neueda.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphMetadata;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphMetadata;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourcesComponent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.database.DatabaseManagerService;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.ConsoleToolWindow;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.util.NameUtil;
import com.neueda.jetbrains.plugin.graphdb.platform.GraphConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

import static java.util.Objects.nonNull;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/3 16:07
 */
public class SQLConsoleSpaceSelectAction extends ComboBoxAction implements DumbAware {

    private DataSourceApi dataSourceApi;

    private Project project;

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        if (Objects.isNull(dataSourceApi)) return actionGroup;

        DataSourceMetadata metadata = DataContext.getInstance(this.project).getMetadata(dataSourceApi);
        if (metadata == null || !(metadata instanceof NebulaDataSourceMetadata)) return actionGroup;
        List<NebulaSpace> nebulaSpaceList = ((NebulaDataSourceMetadata) metadata).getNebulaGraphMetadata().getNebulaSpaceList();
        if (nebulaSpaceList == null || nebulaSpaceList.isEmpty()) {
            return actionGroup;
        }

        for (NebulaSpace nebulaSpace : nebulaSpaceList) {
            actionGroup.add(new AnAction(nebulaSpace.getSpaceName()) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    switchSpace(e, nebulaSpace.getSpaceName());
                }
            });
        }
        return actionGroup;
    }

    private void switchSpace(AnActionEvent e, String space) {
        MessageBus messageBus = project.getMessageBus();
        messageBus.syncPublisher(CommonConsoleLogEvent.COMMON_LOG_EVENT_TOPIC)
                .info("switch space to : " + space);

        ExecuteQueryEvent executeQueryEvent = messageBus.syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC);
        executeQueryEvent.executeQuery(dataSourceApi, new ExecuteQueryPayload("USE " + space));

        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        DataContext.getInstance(project).setCurrentSpace(file.getName(), space);
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
        String selectSpaceName = DataContext.getInstance(project).getCurrentSpace(virtualFile.getName());
        if (selectSpaceName != null) {
            Presentation presentation = e.getPresentation();
            presentation.setText(selectSpaceName, false);
            presentation.setEnabled(true);
            presentation.setVisible(true);
            presentation.setDescription("Switch space");
        }
    }

}
