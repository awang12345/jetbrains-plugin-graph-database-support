package com.vesoft.jetbrains.plugin.graphdb.jetbrains.context;

import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.NameUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/3 20:13
 */
public class DataContext {

    private Map<String, String> currentSpace = new ConcurrentHashMap<>();
    private Map<String, DataSourceApi> dataSourceApiMap = new ConcurrentHashMap<>();

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

    public DataSourceMetadata getMetadataByFileName(String fileName) {
        return metadataMap.get(NameUtil.extractDataSourceUUID(fileName));
    }

    public void addMetadata(DataSourceApi dataSourceApi, DataSourceMetadata metadata) {
        metadataMap.put(dataSourceApi.getUUID(), metadata);
    }

    public void addDataSourceApi(DataSourceApi dataSourceApi) {
        dataSourceApiMap.put(dataSourceApi.getUUID(), dataSourceApi);
    }

    public DataSourceApi getDataSourceApiByFileName(String fileName) {
        return dataSourceApiMap.get(NameUtil.extractDataSourceUUID(fileName));
    }

}
