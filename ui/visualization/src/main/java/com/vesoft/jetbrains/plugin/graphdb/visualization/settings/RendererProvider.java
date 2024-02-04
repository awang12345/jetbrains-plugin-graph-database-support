package com.vesoft.jetbrains.plugin.graphdb.visualization.settings;

import com.vesoft.jetbrains.plugin.graphdb.visualization.constants.VisualizationParameters;
import com.vesoft.jetbrains.plugin.graphdb.visualization.renderers.CustomEdgeRenderer;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;

import static com.vesoft.jetbrains.plugin.graphdb.visualization.constants.GraphColumns.TITLE;
import static com.vesoft.jetbrains.plugin.graphdb.visualization.constants.VisualizationParameters.NODE_DIAMETER;
import static prefuse.Constants.EDGE_TYPE_LINE;

public class RendererProvider {

    private static final int TEXT_OVERLAP = 12;
    private static final int EDGE_TEXT_OVERLAP = 24;

    public static LabelRenderer labelRenderer() {
        LabelRenderer labelRenderer = new LabelRenderer(TITLE);
        labelRenderer.setMaxTextWidth(NODE_DIAMETER);
        return labelRenderer;
    }

    public static LabelRenderer edgeLabelRenderer() {
        LabelRenderer labelRenderer = new LabelRenderer(TITLE);
        return labelRenderer;
    }

    public static ShapeRenderer nodeRenderer() {
        ShapeRenderer nodeRenderer = new ShapeRenderer();
        nodeRenderer.setBaseSize(NODE_DIAMETER);

        return nodeRenderer;
    }

    public static EdgeRenderer edgeRenderer() {
        EdgeRenderer edgeRenderer = new CustomEdgeRenderer(EDGE_TYPE_LINE);
        edgeRenderer.setDefaultLineWidth(VisualizationParameters.EDGE_THICKNESS);
        edgeRenderer.setArrowHeadSize(10, 15);

        return edgeRenderer;
    }
}
