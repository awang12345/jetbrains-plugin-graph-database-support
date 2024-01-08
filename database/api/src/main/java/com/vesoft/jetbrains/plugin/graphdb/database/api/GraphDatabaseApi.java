package com.vesoft.jetbrains.plugin.graphdb.database.api;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphMetadata;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;

import java.util.Map;

public interface GraphDatabaseApi {

    GraphQueryResult execute(String query);

    GraphQueryResult execute(String query, Map<String, Object> statementParameters);

    GraphMetadata metadata();
}
