package com.vesoft.jetbrains.plugin.graphdb.test.integration.neo4j.tests.cypher.parsing;

import com.vesoft.jetbrains.plugin.graphdb.test.integration.neo4j.tests.cypher.util.BaseParsingTest;

public class CommentsParsingTest extends BaseParsingTest {

    public CommentsParsingTest() {
        super("comments");
    }

    public void testBlock() {
        doTest(true);
    }

    public void testLine() {
        doTest(true);
    }
}
