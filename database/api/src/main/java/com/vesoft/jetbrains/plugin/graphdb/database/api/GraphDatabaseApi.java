package com.vesoft.jetbrains.plugin.graphdb.database.api;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphMetadata;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;

import java.util.Map;
import java.util.function.Consumer;

public interface GraphDatabaseApi {

    GraphQueryResult execute(String query);

    GraphQueryResult execute(String query, Map<String, Object> statementParameters);

    default GraphMetadata metadata() {
        return metadata(null, null);
    }

    GraphMetadata metadata(Consumer<String> progressDetailDisplay, Consumer<Float> progressPercentageDisplay);

    default void close() {
    }
    
}
