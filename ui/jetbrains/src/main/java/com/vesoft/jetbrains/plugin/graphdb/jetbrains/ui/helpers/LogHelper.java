package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers;

import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/12 11:17
 */
public class LogHelper {

    public static CommonConsoleLogEvent create(Project project) {
        return project.getMessageBus().syncPublisher(CommonConsoleLogEvent.COMMON_LOG_EVENT_TOPIC);
    }

}
