package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.event;

import com.intellij.util.messages.Topic;

public interface CommonConsoleLogEvent {

    Topic<CommonConsoleLogEvent> COMMON_LOG_EVENT_TOPIC = Topic.create("GraphDatabaseConsole.CommonLogEvent", CommonConsoleLogEvent.class);

     void info(String msg);

     void warn(String msg);

     void error(String msg, Exception throwable);
}
