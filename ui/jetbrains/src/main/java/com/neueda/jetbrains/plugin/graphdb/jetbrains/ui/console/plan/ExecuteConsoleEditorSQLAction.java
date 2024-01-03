package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourcesComponent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.util.NameUtil;
import com.neueda.jetbrains.plugin.graphdb.platform.GraphConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class ExecuteConsoleEditorSQLAction extends AnAction implements Disposable {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        // execute
        execute(editor, file, project);
    }

    private void execute(Editor editor, VirtualFile file, Project project) {
        if (Objects.nonNull(editor) && Objects.nonNull(file)) {
            if (!file.getName().startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX)) return;
            DataSourcesComponent dataSourcesComponent = project.getComponent(DataSourcesComponent.class);
            // 优先支持选中执行
            String selectedText = StringUtils.defaultString(editor.getSelectionModel().getSelectedText(), editor.getDocument().getText());
            String nGql = clearComment(selectedText);
            Optional<? extends DataSourceApi> boundDataSource = dataSourcesComponent.getDataSourceContainer()
                    .findDataSource(NameUtil.extractDataSourceUUID(file.getName()));
            if (boundDataSource.isPresent()) {
                executeQuery(project.getMessageBus(), boundDataSource.get(), new ExecuteQueryPayload(nGql, Collections.emptyMap(), editor));
            }
        }
    }


    private void executeQuery(MessageBus messageBus, DataSourceApi dataSource, ExecuteQueryPayload payload) {
        ExecuteQueryEvent executeQueryEvent = messageBus.syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC);
        executeQueryEvent.executeQuery(dataSource, payload);
    }

    private String clearComment(String sqlContent) {
        String[] sqlLines = sqlContent.split("\n");
        return Arrays.stream(sqlLines).filter(sqlLine -> {
            // -- 和 # 开头的为注释行
            sqlLine = sqlLine.trim();
            if (sqlLine.startsWith("--") || sqlLine.startsWith("#")) return false;
            // 空行
            String trim = sqlLine.replaceAll("\n", "").trim();
            return !StringUtils.isEmpty(trim);
        }).collect(Collectors.joining(" "));
    }

    @Override
    public void dispose() {

    }
}
