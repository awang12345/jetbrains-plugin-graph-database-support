package com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute;

public class ExplainQueryAction extends ExecuteQueryAction {

    @Override
    protected String decorateQuery(String query) {
        return "EXPLAIN " + super.decorateQuery(query);
    }
}
