package com.vesoft.jetbrains.plugin.graphdb.database.api.data;

public interface GraphNode extends GraphEntity {

    default String getRepresentation() {
        return "Node[" + getId() + "]";
    }
}
