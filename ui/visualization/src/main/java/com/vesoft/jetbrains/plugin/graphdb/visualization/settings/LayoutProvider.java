package com.vesoft.jetbrains.plugin.graphdb.visualization.settings;

import com.vesoft.jetbrains.plugin.graphdb.visualization.GraphDisplay;
import com.vesoft.jetbrains.plugin.graphdb.visualization.layouts.CenteredLayout;
import com.vesoft.jetbrains.plugin.graphdb.visualization.layouts.DynamicForceLayout;
import com.vesoft.jetbrains.plugin.graphdb.visualization.layouts.RepaintAndRepositionAction;
import com.vesoft.jetbrains.plugin.graphdb.visualization.services.LookAndFeelService;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.activity.Activity;

import static com.vesoft.jetbrains.plugin.graphdb.visualization.constants.GraphGroups.EDGE_LABEL;
import static com.vesoft.jetbrains.plugin.graphdb.visualization.constants.GraphGroups.GRAPH;
import static com.vesoft.jetbrains.plugin.graphdb.visualization.constants.GraphGroups.NODE_LABEL;

public class LayoutProvider {

    private static final boolean ENFORCE_BOUNDS = false;

    public static ActionList forceLayout(Visualization viz, GraphDisplay display, LookAndFeelService lookAndFeel) {
        ActionList actions = new ActionList(viz);

        actions.add(new DynamicForceLayout(GRAPH, ENFORCE_BOUNDS));
        actions.add(ColorProvider.colors(lookAndFeel));
        actions.add(new RepaintAndRepositionAction(viz, display));

        return actions;
    }

    public static ActionList repaintLayout(LookAndFeelService lookAndFeelService) {
        ActionList repaint = new ActionList(Activity.INFINITY);
        repaint.add(new CenteredLayout(NODE_LABEL));
        repaint.add(new CenteredLayout(EDGE_LABEL));
        repaint.add(ColorProvider.colors(lookAndFeelService));
        repaint.add(new RepaintAction());

        return repaint;
    }
}
