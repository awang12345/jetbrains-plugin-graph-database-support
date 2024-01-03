package com.neueda.jetbrains.plugin.graphdb.jetbrains.context;

import com.intellij.openapi.project.Project;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/3 20:13
 */
public class DataContext {

    private Map<String, String> currentSpace;

    private Map<String, DataSourceMetadata> metadataMap = new ConcurrentHashMap<>();

    public static DataContext getInstance(Project project) {
        return project.getService(DataContext.class);
    }

    public String getCurrentSpace(String fileName) {
        return currentSpace.get(fileName);
    }

    public void setCurrentSpace(String fileName, String currentSpace) {
        this.currentSpace.put(fileName, currentSpace);
    }

    public DataSourceMetadata getMetadata(DataSourceApi dataSourceApi) {
        return metadataMap.get(dataSourceApi.getUUID());
    }

    public void addMetadata(DataSourceApi dataSourceApi, DataSourceMetadata metadata) {
        metadataMap.put(dataSourceApi.getUUID(), metadata);
    }
}
