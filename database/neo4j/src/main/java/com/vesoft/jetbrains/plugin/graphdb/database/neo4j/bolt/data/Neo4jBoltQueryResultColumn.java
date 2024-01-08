package com.vesoft.jetbrains.plugin.graphdb.database.neo4j.bolt.data;

import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;

public class Neo4jBoltQueryResultColumn implements GraphQueryResultColumn {

    private final String name;

    public Neo4jBoltQueryResultColumn(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
