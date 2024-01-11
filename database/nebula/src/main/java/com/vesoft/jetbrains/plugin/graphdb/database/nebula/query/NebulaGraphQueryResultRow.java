package com.vesoft.jetbrains.plugin.graphdb.database.nebula.query;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultRow;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphRelationship;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                return NebulaValueToString.NULL_VALUE;
            }
            if (wrapper.isVertex()) {
                return new NebulaGraphNode(wrapper.asNode());
            }
            if (wrapper.isEdge()) {
                return new NebulaGraphRelationship(wrapper.asRelationship());
            }
            if (wrapper.isList()) {
                List<Object> list = new ArrayList<>();
                for (ValueWrapper vw : wrapper.asList()) {
                    list.add(vw.asString());
                }
                return list;
            }
            if (wrapper.isMap()) {
                HashMap<String, String> map = new HashMap<>();
                HashMap<String, ValueWrapper> valueMap = wrapper.asMap();
                for (Map.Entry<String, ValueWrapper> entry : valueMap.entrySet()) {
                    map.put(entry.getKey(), NebulaValueToString.valueToString(entry.getValue().getValue()));
                }
                return map;
            }
            return NebulaValueToString.valueToString(wrapper.getValue());
        } catch (Exception ex) {
            return "Parse error:" + ex.getMessage();
        }
    }


    @Override
    public List<GraphNode> getNodes() {
        List<GraphNode> graphNodeList = new ArrayList<>();
        for (ValueWrapper value : record.values()) {
            addNode(graphNodeList, value);
        }
        return graphNodeList;
    }

    private static void addNode(List<GraphNode> graphNodeList, ValueWrapper value) {
        if (value.isEdge()) {
            Relationship relationship = value.asRelationship();
            graphNodeList.add(new NebulaGraphNode(NebulaValueToString.getVidString(relationship.srcId().getValue())));
            graphNodeList.add(new NebulaGraphNode(NebulaValueToString.getVidString(relationship.dstId().getValue())));
        } else if (value.isPath()) {
            try {
                List<Node> nodes = value.asPath().getNodes();
                nodes.stream().map(NebulaGraphNode::new).forEach(graphNodeList::add);
            } catch (UnsupportedEncodingException e) {
            }
        } else if (value.isVertex()) {
            graphNodeList.add(new NebulaGraphNode(value.getValue().getVVal()));
        } else if (value.isList()) {
            value.asList().forEach(v -> addNode(graphNodeList, v));
        } else if (value.isSet()) {
            value.asSet().forEach(v -> addNode(graphNodeList, v));
        }
    }

    @Override
    public List<GraphRelationship> getRelationships() {
        List<GraphRelationship> graphRelationshipList = new ArrayList<>();
        for (ValueWrapper valueWrapper : record.values()) {
            addRelationShip(graphRelationshipList, valueWrapper);
        }
        return graphRelationshipList;
    }

    private static void addRelationShip(List<GraphRelationship> graphRelationshipList, ValueWrapper valueWrapper) {
        if (valueWrapper.isPath()) {
            try {
                valueWrapper.asPath().getRelationships().stream().map(NebulaGraphRelationship::new).forEach(graphRelationshipList::add);
            } catch (UnsupportedEncodingException e) {
            }
        } else if (valueWrapper.isEdge()) {
            graphRelationshipList.add(new NebulaGraphRelationship(valueWrapper.asRelationship()));
        } else if (valueWrapper.isList()) {
            valueWrapper.asList().forEach(value -> addRelationShip(graphRelationshipList, value));
        } else if (valueWrapper.isSet()) {
            valueWrapper.asSet().forEach(value -> addRelationShip(graphRelationshipList, value));
        }
    }
}
