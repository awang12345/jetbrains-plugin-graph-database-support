package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree;

import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.*;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.ContextMenu;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.dto.MetadataContextMenu;
import icons.GraphIcons;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NebulaMetadataTreeNodeModel implements TreeNodeModelApi {

    private MetadataContextMenu metadataContextMenu;
    private NodeType type;
    private Icon icon;
    private String value;
    private DataSourceApi dataSourceApi;

    private Object data;

    private Optional<String> textAttribute;

    public NebulaMetadataTreeNodeModel(NebulaTreeNodeType type, DataSourceApi dataSourceApi, String value, Icon icon, Object data) {
        this.type = type;
        this.value = value;
        this.dataSourceApi = dataSourceApi;
        this.icon = icon;
        this.data = data;
        this.textAttribute = Optional.ofNullable(getNodeAttributeText(data));
        prepareContextMenu();
    }

    private void prepareContextMenu() {
        if (type == NebulaTreeNodeType.SPACE
                || type == NebulaTreeNodeType.EDGE
                || type == NebulaTreeNodeType.TAG) {
            metadataContextMenu = new MetadataContextMenu(type, getDataSourceApi(), value, this.data);
        }
    }

    @Override
    public Optional<String> getTextAttribute() {
        return this.textAttribute;
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

    public NebulaSchema getSchema() {
        if (data instanceof NebulaSchema) {
            return (NebulaSchema) data;
        }
        return null;
    }

    private String getNodeAttributeText(Object node) {
        if (node instanceof NebulaSpace) {
            NebulaSpace nebulaSpace = (NebulaSpace) node;
            String id = StringUtils.isNotBlank(nebulaSpace.getId()) ? "[" + nebulaSpace.getId() + "] " : StringUtils.EMPTY;
            return String.format("%s%s", id, formatDesc(nebulaSpace.getType(), nebulaSpace.getDataCount(), nebulaSpace.getComment()));
        }
        if (node instanceof NebulaTag) {
            NebulaTag nebulaTag = (NebulaTag) node;
            String id = StringUtils.isNotBlank(nebulaTag.getId()) ? "[" + nebulaTag.getId() + "] " : StringUtils.EMPTY;
            return String.format("%s%s", id, formatDesc(nebulaTag.getDataCount(), nebulaTag.getComment()));
        }
        if (node instanceof NebulaEdge) {
            NebulaEdge nebulaEdge = (NebulaEdge) node;
            String id = StringUtils.isNotBlank(nebulaEdge.getId()) ? "[" + nebulaEdge.getId() + "] " : StringUtils.EMPTY;
            return String.format("%s%s", id, formatDesc(nebulaEdge.getDataCount(), nebulaEdge.getComment()));
        }
        if (node instanceof NebulaField) {
            NebulaField field = (NebulaField) node;
            return formatDesc(field.getType(), field.getComment());
        }
        return null;
    }

    private String formatDesc(Object... args) {
        String desc = Arrays.stream(args).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(","));
        return StringUtils.isBlank(desc) ? "" : String.format("(%s)", desc);
    }
}
