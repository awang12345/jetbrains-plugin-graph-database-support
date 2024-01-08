package com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.ui.console;

import com.intellij.util.messages.Topic;

public interface CleanCanvasEvent {

    Topic<CleanCanvasEvent> CLEAN_CANVAS_TOPIC = Topic.create("GraphDatabaseConsole.CleanCanvasTopic", CleanCanvasEvent.class);

    void cleanCanvas();
}
