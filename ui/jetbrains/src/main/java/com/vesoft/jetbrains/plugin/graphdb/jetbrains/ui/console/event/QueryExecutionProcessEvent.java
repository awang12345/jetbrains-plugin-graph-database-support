package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event;

import com.intellij.util.messages.Topic;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;

public interface QueryExecutionProcessEvent {

    Topic<QueryExecutionProcessEvent> QUERY_EXECUTION_PROCESS_TOPIC =
            Topic.create("GraphDatabaseConsole.QueryExecutionProcessTopic", QueryExecutionProcessEvent.class);

    default void executionStarted(DataSourceApi dataSource, ExecuteQueryPayload payload){};

    default void resultReceived(ExecuteQueryPayload payload, GraphQueryResult result){};

    default void postResultReceived(ExecuteQueryPayload payload){};

    default void handleError(ExecuteQueryPayload payload, Exception exception){};

    default void executionCompleted(ExecuteQueryPayload payload){};
}
