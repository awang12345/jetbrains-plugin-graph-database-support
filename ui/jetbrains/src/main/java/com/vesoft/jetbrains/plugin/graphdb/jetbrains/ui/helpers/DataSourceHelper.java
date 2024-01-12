package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers;

import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourcesComponent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.NameUtil;
import com.vesoft.jetbrains.plugin.graphdb.platform.GraphConstants;

import java.util.Optional;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/12 11:07
 */
public class DataSourceHelper {

    public static Optional<DataSourceApi> getDataSourceApi(String fileName, Project project) {
        if (project == null || fileName == null) {
            return Optional.empty();
        }
        if (fileName.startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX)) {
            Optional<? extends DataSourceApi> boundDataSource = project
                    .getComponent(DataSourcesComponent.class)
                    .getDataSourceContainer()
                    .findDataSource(NameUtil.extractDataSourceUUID(fileName));
            if (boundDataSource.isPresent()) {
                return Optional.ofNullable(boundDataSource.get());
            }
        }
        return Optional.empty();
    }

}
