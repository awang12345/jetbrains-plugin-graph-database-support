package com.neueda.jetbrains.plugin.graphdb.database.nebula.query;

import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.neueda.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;
import com.neueda.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultRow;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphRelationship;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 14:23
 */
public class NebulaGraphQueryResultRow implements GraphQueryResultRow {

    private ResultSet.Record record;

    public NebulaGraphQueryResultRow(ResultSet.Record record) {
        this.record = record;
    }

    @Override
    public Object getValue(GraphQueryResultColumn column) {
        Object fieldValue = this.record.get(column.getName()).getValue().getFieldValue();
        return fieldValue;
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
