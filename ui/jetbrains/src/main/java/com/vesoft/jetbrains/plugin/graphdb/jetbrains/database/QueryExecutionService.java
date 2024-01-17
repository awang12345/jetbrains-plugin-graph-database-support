package com.vesoft.jetbrains.plugin.graphdb.jetbrains.database;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.analytics.Analytics;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.QueryExecutionProcessEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.QueryPlanEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.Notifier;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

public class QueryExecutionService {

    private final DatabaseManagerService databaseManager;
    private final MessageBus messageBus;
    private Future<?> runningQuery;
    private final Project project;

    public QueryExecutionService(Project project, MessageBus messageBus) {
        this.messageBus = messageBus;
        this.databaseManager = ServiceManager.getService(DatabaseManagerService.class);
        this.project = project;
    }

    public void executeQuery(DataSourceApi dataSource, ExecuteQueryPayload payload) {
        checkForRunningQuery();
        try {
            checkForNebula(dataSource, payload);
            executeInBackground(dataSource, payload);
        } catch (Exception e) {
            Notifier.error("Query execution", "Error during execution: " + e.toString());
        } finally {
            Analytics.event(dataSource, "executeQuery");
        }
    }

    private void checkForNebula(DataSourceApi dataSource, ExecuteQueryPayload payload) {
        if (dataSource.getDataSourceType() == DataSourceType.NEBULA) {
            String currentSpace = DataContext.getInstance(project).getCurrentSpace(dataSource);
            if (StringUtils.isNotBlank(currentSpace)) {
                return;
            }

            String sql = payload.getQueries().get(0).trim().toLowerCase();
            if (!sql.startsWith("use ")
                    && !sql.startsWith("create space ")
                    && !sql.startsWith("show ")
                    && !sql.startsWith("desc ")) {
                throw new IllegalArgumentException("Please select a nebula space first.");
            }
        }
    }

    private synchronized void checkForRunningQuery() {
        if (runningQuery == null) {
            return;
        }

        if (runningQuery.isDone()) {
            runningQuery = null;
        } else {
            runningQuery.cancel(true);
        }
    }

    private synchronized void executeInBackground(DataSourceApi dataSource, ExecuteQueryPayload payload) {
        QueryExecutionProcessEvent event = messageBus.syncPublisher(QueryExecutionProcessEvent.QUERY_EXECUTION_PROCESS_TOPIC);
        event.executionStarted(dataSource, payload);
        if (payload.getQueries().isEmpty()) {
            return;
        }
        new Task.Backgroundable(project, "Execute graph sql...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Execute graph sql: " + payload.getQueries().get(0));
                if (payload.getQueries().size() == 1) {
                    executeQuery(dataSource, payload, event).run();
                } else {
                    executeBatch(dataSource, payload, event);
                }
            }
        }.queue();

//        if (payload.getQueries().size() == 1) {
//            runningQuery = ApplicationManager.getApplication()
//                    .executeOnPooledThread(executeQuery(dataSource, payload, event));
//        } else {
//            runningQuery = ApplicationManager.getApplication()
//                    .executeOnPooledThread(executeBatch(dataSource, payload, event));
//        }
    }

    @NotNull
    private Runnable executeQuery(DataSourceApi dataSource, ExecuteQueryPayload payload, QueryExecutionProcessEvent event) {
        return () -> {
            if (payload.getQueries().size() != 1) {
                return;
            }

            try {
                GraphDatabaseApi database = databaseManager.getDatabaseFor(dataSource);

                String query = payload.getQueries().get(0);

                GraphQueryResult result = database.execute(query, payload.getParameters());

                ApplicationManager.getApplication().invokeLater(() -> {

                    event.resultReceived(payload, result);
                    event.postResultReceived(payload);
                    event.executionCompleted(payload);

                    if (result.hasPlan()) {
                        QueryPlanEvent queryPlanEvent = messageBus.syncPublisher(QueryPlanEvent.QUERY_PLAN_EVENT);
                        queryPlanEvent.queryPlanReceived(query, result);
                    }
                });
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    event.handleError(payload, e);
                    event.executionCompleted(payload);
                });
            }
        };
    }

    @NotNull
    private Runnable executeBatch(DataSourceApi dataSource, ExecuteQueryPayload payload, QueryExecutionProcessEvent event) {
        return () -> {
            try {
                GraphDatabaseApi database = databaseManager.getDatabaseFor(dataSource);
                for (String query : payload.getQueries()) {
                    database.execute(query, payload.getParameters());
                }
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    event.handleError(payload, e);
                    event.executionCompleted(payload);
                });
            }
        };
    }
}
