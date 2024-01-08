package com.vesoft.jetbrains.plugin.graphdb.test.integration.neo4j.tests.cypher.rename;


import com.vesoft.jetbrains.plugin.graphdb.test.integration.neo4j.tests.cypher.util.BaseRenameTest;

public class RelationshipTypeRenameTest extends BaseRenameTest {

    public RelationshipTypeRenameTest() {
        super("relationship_type");
    }

    public void testSingleQuery() {
        verify("RENAMED_TYPE");
    }

    public void testMultipleQueries() {
        verify("RENAMED_TYPE");
    }
}
