package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree;

import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.ContextMenu;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.MetadataContextMenu;

import javax.swing.*;
import java.util.Optional;

public class NebulaMetadataTreeNodeModel implements TreeNodeModelApi {

    private MetadataContextMenu metadataContextMenu;
    private NodeType type;
    private Icon icon;
    private String value;
    private DataSourceApi dataSourceApi;

    private Object extData;

    public NebulaMetadataTreeNodeModel(NebulaTreeNodeType type, DataSourceApi dataSourceApi, String value) {
        this(type, dataSourceApi, value, null, null);
    }

    public NebulaMetadataTreeNodeModel(NebulaTreeNodeType type, DataSourceApi dataSourceApi, String value, Icon icon) {
        this(type, dataSourceApi, value, icon, null);
    }

    public NebulaMetadataTreeNodeModel(NebulaTreeNodeType type, DataSourceApi dataSourceApi, String value, Icon icon, Object extData) {
        this.type = type;
        this.value = value;
        this.dataSourceApi = dataSourceApi;
        this.icon = icon;
        this.extData = extData;
        prepareContextMenu();
    }

    private void prepareContextMenu() {
        if (type == NebulaTreeNodeType.SPACE
                || type == NebulaTreeNodeType.EDGE
                || type == NebulaTreeNodeType.TAG) {
            metadataContextMenu = new MetadataContextMenu(type, getDataSourceApi(), value, this.extData);
        }
    }

    public Optional<ContextMenu> getContextMenu() {
        return Optional.ofNullable(metadataContextMenu);
    }

    @Override
    public NodeType getType() {
        return type;
    }

    public void setType(Neo4jTreeNodeType type) {
        this.type = type;
    }

    @Override
    public Optional<Icon> getIcon() {
        return Optional.ofNullable(icon);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public Optional<String> getText() {
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<Object> getValue() {
        return Optional.ofNullable(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public DataSourceApi getDataSourceApi() {
        return dataSourceApi;
    }
}
