package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree;

import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.ContextMenu;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.DataSourceContextMenu;

import javax.swing.*;
import java.util.Optional;

public class DataSourceTreeNodeModel implements TreeNodeModelApi {

    private DataSourceApi dataSourceApi;
    private DataSourceContextMenu dataSourceContextMenu;

    private NodeType dataSourceType;

    public DataSourceTreeNodeModel(DataSourceApi dataSourceApi) {
        this.dataSourceApi = dataSourceApi;
        this.dataSourceContextMenu = new DataSourceContextMenu(dataSourceApi);
        switch (dataSourceApi.getDataSourceType()) {
            case NEBULA:
                this.dataSourceType = NebulaTreeNodeType.DATASOURCE;
                break;
            default:
                this.dataSourceType = Neo4jTreeNodeType.DATASOURCE;
        }
    }

    public Optional<ContextMenu> getContextMenu() {
        return Optional.of(dataSourceContextMenu);
    }

    @Override
    public NodeType getType() {
        return this.dataSourceType;
    }

    @Override
    public Optional<Icon> getIcon() {
        return Optional.ofNullable(dataSourceApi.getDescription().getIcon());
    }

    @Override
    public Optional<String> getText() {
        return Optional.ofNullable(dataSourceApi.getName());
    }

    @Override
    public Optional<Object> getValue() {
        return Optional.empty();
    }

    @Override
    public DataSourceApi getDataSourceApi() {
        return dataSourceApi;
    }
}
