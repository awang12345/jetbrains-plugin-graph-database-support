package com.vesoft.jetbrains.plugin.graphdb.visualization.events;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import prefuse.visual.VisualItem;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface NodeCallback {
    void accept(GraphNode node, VisualItem item, MouseEvent e);
}
