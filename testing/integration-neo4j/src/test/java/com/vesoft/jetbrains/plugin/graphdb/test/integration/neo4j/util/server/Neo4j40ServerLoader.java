package com.vesoft.jetbrains.plugin.graphdb.test.integration.neo4j.util.server;

import com.vesoft.jetbrains.plugin.graphdb.test.database.neo4j.common.Neo4jServer;

public class Neo4j40ServerLoader extends Neo4jServerLoader {

    private static Neo4j40ServerLoader neo4jServer;

    public static synchronized Neo4jServer getInstance() {
        if (neo4jServer == null) {
            neo4jServer = new Neo4j40ServerLoader();
            neo4jServer.start();
        }
        return neo4jServer;
    }

    @Override
    protected String getLibraryPath() {
        return System.getProperty("neo4j-package-4.0");
    }

    @Override
    protected String getNeo4jServerClass() {
        return "com.vesoft.jetbrains.plugin.graphdb.test.database.neo4j_4_0.Neo4j40Server";
    }
}
