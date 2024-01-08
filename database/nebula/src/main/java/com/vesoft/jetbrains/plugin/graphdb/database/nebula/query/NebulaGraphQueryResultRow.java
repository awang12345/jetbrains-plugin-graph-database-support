package com.vesoft.jetbrains.plugin.graphdb.database.nebula.query;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultRow;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphRelationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class NebulaGraphQueryResultRow implements GraphQueryResultRow {

    private ResultSet.Record record;

    public NebulaGraphQueryResultRow(ResultSet.Record record) {
        this.record = record;
    }

    @Override
    public Object getValue(GraphQueryResultColumn column) {
        try {
            ValueWrapper wrapper = this.record.get(column.getName());
            if (wrapper.isString()) {
                return wrapper.asString();
            }
            if (wrapper.isNull()) {
                return "NULL";
            }
            return String.valueOf(wrapper.getValue().getFieldValue());
        } catch (Exception ex) {
            return "Parse error:" + ex.getMessage();
        }
    }

    @Override
    public List<GraphNode> getNodes() {
        return new ArrayList<>();
    }

    @Override
    public List<GraphRelationship> getRelationships() {
        return record.values().stream()
                .filter(ValueWrapper::isEdge)
                .map(ValueWrapper::asRelationship)
                .map(NebulaGraphRelationship::new)
                .collect(Collectors.toList());
    }
}
