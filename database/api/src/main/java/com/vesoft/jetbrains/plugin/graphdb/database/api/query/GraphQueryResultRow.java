package com.vesoft.jetbrains.plugin.graphdb.database.api.query;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface GraphQueryResultRow {

    Object getValue(GraphQueryResultColumn column) ;

    List<GraphNode> getNodes();

    List<GraphRelationship> getRelationships();
}
