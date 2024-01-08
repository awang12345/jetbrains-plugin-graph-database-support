package com.vesoft.jetbrains.plugin.graphdb.jetbrains.database;

import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;

public interface DatabaseManagerService {

    GraphDatabaseApi getDatabaseFor(DataSourceApi dataSource);
}
