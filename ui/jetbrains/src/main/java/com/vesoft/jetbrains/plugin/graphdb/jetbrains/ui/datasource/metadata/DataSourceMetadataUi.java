package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.*;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourcesComponentMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.Neo4jBoltCypherDataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.*;
import icons.GraphIcons;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType.*;
import static com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.Neo4jTreeNodeType.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class DataSourceMetadataUi {

    private static final String RELATIONSHIP_TYPES_TITLE = "relationship types (%s)";
    private static final String PROPERTY_KEYS_TITLE = "property keys";
    private static final String LABELS_TITLE = "labels (%s)";
    private static final String STORED_PROCEDURES_TITLE = "stored procedures";
    private static final String USER_FUNCTIONS_TITLE = "user functions";
    private static final String INDEXES_TITLE = "indexes (%s)";
    private static final String CONSTRAINTS_TITLE = "constraints (%s)";

    private final DataSourcesComponentMetadata dataSourcesComponent;

    private final Map<DataSourceType, BiFunction<PatchedDefaultMutableTreeNode, DataSourceMetadata, Boolean>> handlers = new HashMap<>();

    public DataSourceMetadataUi(DataSourcesComponentMetadata dataSourcesComponent) {
        this.dataSourcesComponent = dataSourcesComponent;

        handlers.put(NEO4J_BOLT, this::updateNeo4jBoltCypherMetadataUi);
        handlers.put(OPENCYPHER_GREMLIN, this::updateOpenCypherGremlinMetadataUi);
        handlers.put(NEBULA, this::updateNebulaMetadataUi);
    }

    public CompletableFuture<Boolean> updateDataSourceMetadataUi(PatchedDefaultMutableTreeNode node, DataSourceApi nodeDataSource, Project project) {
        DataSourceType sourceType = nodeDataSource.getDataSourceType();
        if (handlers.containsKey(sourceType)) {
            return dataSourcesComponent.getMetadata(nodeDataSource, project)
                    .thenApply((data) ->
                            data.map(metadata -> handlers.get(sourceType).apply(node, metadata))
                                    .orElse(false));
        } else {
            return completedFuture(false);
        }
    }

    boolean updateOpenCypherGremlinMetadataUi(PatchedDefaultMutableTreeNode dataSourceRootTreeNode,
                                              DataSourceMetadata metadata) {
        Neo4jBoltCypherDataSourceMetadata dataSourceMetadata = (Neo4jBoltCypherDataSourceMetadata) metadata;
        // Remove existing metadata from ui
        dataSourceRootTreeNode.removeAllChildren();
        TreeNodeModelApi model = (TreeNodeModelApi) dataSourceRootTreeNode.getUserObject();
        DataSourceApi dataSourceApi = model.getDataSourceApi();

        dataSourceRootTreeNode.add(createLabelsNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createRelationshipTypesNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createPropertyKeysNode(dataSourceMetadata, dataSourceApi));

        return true;
    }

    boolean updateNebulaMetadataUi(PatchedDefaultMutableTreeNode dataSourceRootTreeNode,
                                   DataSourceMetadata dataSourceMetadata) {
        // Remove existing metadata from ui
        dataSourceRootTreeNode.removeAllChildren();
        TreeNodeModelApi model = (TreeNodeModelApi) dataSourceRootTreeNode.getUserObject();
        DataSourceApi dataSourceApi = model.getDataSourceApi();
        addNebulaMetaNode(dataSourceRootTreeNode, (NebulaDataSourceMetadata) dataSourceMetadata, dataSourceApi);
        return true;
    }

    // ui
    boolean updateNeo4jBoltCypherMetadataUi(PatchedDefaultMutableTreeNode dataSourceRootTreeNode,
                                            DataSourceMetadata metadata) {

        Neo4jBoltCypherDataSourceMetadata dataSourceMetadata = (Neo4jBoltCypherDataSourceMetadata) metadata;
        // Remove existing metadata from ui
        dataSourceRootTreeNode.removeAllChildren();
        TreeNodeModelApi model = (TreeNodeModelApi) dataSourceRootTreeNode.getUserObject();
        DataSourceApi dataSourceApi = model.getDataSourceApi();

        dataSourceRootTreeNode.add(createConstraintsNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createIndexesNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createLabelsNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createRelationshipTypesNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createPropertyKeysNode(dataSourceMetadata, dataSourceApi));
        dataSourceRootTreeNode.add(createStoredProceduresNode(dataSourceMetadata, dataSourceApi));

        if (dataSourceMetadata.isMetadataExists(Neo4jBoltCypherDataSourceMetadata.USER_FUNCTIONS)) {
            dataSourceRootTreeNode.add(createUserFunctionNode(dataSourceMetadata, dataSourceApi));
        }

        return true;
    }

    @NotNull
    private PatchedDefaultMutableTreeNode createUserFunctionNode(Neo4jBoltCypherDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        PatchedDefaultMutableTreeNode userFunctionTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(USER_FUNCTIONS, dataSourceApi, USER_FUNCTIONS_TITLE, GraphIcons.Nodes.USER_FUNCTION));

        dataSourceMetadata
                .getMetadata(Neo4jBoltCypherDataSourceMetadata.USER_FUNCTIONS)
                .forEach((row) -> {
                    PatchedDefaultMutableTreeNode nameNode = of(new MetadataTreeNodeModel(USER_FUNCTION, dataSourceApi, row.get("name")));
                    PatchedDefaultMutableTreeNode descriptionNode = of(new MetadataTreeNodeModel(USER_FUNCTION, dataSourceApi, row.get("signature")));
                    nameNode.add(descriptionNode);
                    userFunctionTreeNode.add(nameNode);
                });
        return userFunctionTreeNode;
    }

    @NotNull
    private PatchedDefaultMutableTreeNode createStoredProceduresNode(Neo4jBoltCypherDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        PatchedDefaultMutableTreeNode storedProceduresTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(STORED_PROCEDURES, dataSourceApi, STORED_PROCEDURES_TITLE, GraphIcons.Nodes.STORED_PROCEDURE));
        dataSourceMetadata
                .getMetadata(Neo4jBoltCypherDataSourceMetadata.STORED_PROCEDURES)
                .forEach((row) -> {
                    PatchedDefaultMutableTreeNode nameNode = of(new MetadataTreeNodeModel(STORED_PROCEDURE, dataSourceApi, row.get("name")));
                    PatchedDefaultMutableTreeNode descriptionNode = of(new MetadataTreeNodeModel(STORED_PROCEDURE, dataSourceApi, row.get("signature")));
                    nameNode.add(descriptionNode);
                    storedProceduresTreeNode.add(nameNode);
                });
        return storedProceduresTreeNode;
    }

    @NotNull
    private PatchedDefaultMutableTreeNode createPropertyKeysNode(Neo4jBoltCypherDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        PatchedDefaultMutableTreeNode propertyKeysTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(PROPERTY_KEYS, dataSourceApi, PROPERTY_KEYS_TITLE, GraphIcons.Nodes.PROPERTY_KEY));
        dataSourceMetadata
                .getMetadata(Neo4jBoltCypherDataSourceMetadata.PROPERTY_KEYS)
                .forEach((row) -> propertyKeysTreeNode.add(of(new MetadataTreeNodeModel(PROPERTY_KEY, dataSourceApi, row.get("propertyKey")))));
        return propertyKeysTreeNode;
    }

    @NotNull
    private PatchedDefaultMutableTreeNode createRelationshipTypesNode(Neo4jBoltCypherDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        int relationshipTypesCount = dataSourceMetadata.getRelationshipTypes().size();
        String relationshipTypesName = String.format(RELATIONSHIP_TYPES_TITLE, relationshipTypesCount);
        PatchedDefaultMutableTreeNode relationshipTypesTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(RELATIONSHIPS, dataSourceApi, relationshipTypesName, GraphIcons.Nodes.RELATIONSHIP_TYPE));
        dataSourceMetadata.getRelationshipTypes()
                .stream()
                .map(rel -> new RelationshipTypeTreeNodeModel(RELATIONSHIP, dataSourceApi, rel.getName(), rel.getCount()))
                .forEach(relModel -> relationshipTypesTreeNode.add(of(relModel)));
        return relationshipTypesTreeNode;
    }

    @NotNull
    private PatchedDefaultMutableTreeNode createLabelsNode(Neo4jBoltCypherDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        int labelCount = dataSourceMetadata.getLabels().size();
        PatchedDefaultMutableTreeNode labelsTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(LABELS, dataSourceApi, String.format(LABELS_TITLE, labelCount), GraphIcons.Nodes.LABEL));
        dataSourceMetadata.getLabels()
                .stream()
                .map(label -> new LabelTreeNodeModel(LABEL, dataSourceApi, label.getName(), label.getCount()))
                .forEach(labelModel -> labelsTreeNode.add(of(labelModel)));
        return labelsTreeNode;
    }

    private MutableTreeNode createIndexesNode(DataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        List<Map<String, String>> indexesMetadata =
                dataSourceMetadata.getMetadata(Neo4jBoltCypherDataSourceMetadata.INDEXES);
        PatchedDefaultMutableTreeNode indexTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(INDEXES,
                        dataSourceApi,
                        String.format(INDEXES_TITLE, indexesMetadata.size()),
                        GraphIcons.Nodes.INDEX));
        indexesMetadata
                .forEach(row -> indexTreeNode.add(of(new MetadataTreeNodeModel(INDEX, dataSourceApi,
                        row.get("description").substring(6) + " " + row.get("state")))));

        return indexTreeNode;
    }

    private MutableTreeNode createConstraintsNode(DataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        List<Map<String, String>> constraintsMetadata =
                dataSourceMetadata.getMetadata(Neo4jBoltCypherDataSourceMetadata.CONSTRAINTS);
        PatchedDefaultMutableTreeNode indexTreeNode = new PatchedDefaultMutableTreeNode(
                new MetadataTreeNodeModel(CONSTRAINTS, dataSourceApi,
                        String.format(CONSTRAINTS_TITLE, constraintsMetadata.size()), GraphIcons.Nodes.CONSTRAINT));
        constraintsMetadata
                .forEach(row ->
                        indexTreeNode.add(of(new MetadataTreeNodeModel(CONSTRAINT, dataSourceApi,
                                row.get("description").substring(11)))));

        return indexTreeNode;
    }

    private void addNebulaMetaNode(PatchedDefaultMutableTreeNode dataSourceRootTreeNode, NebulaDataSourceMetadata dataSourceMetadata, DataSourceApi dataSourceApi) {
        NebulaGraphMetadata nebulaGraphMetadata = dataSourceMetadata.getNebulaGraphMetadata();
        if (nebulaGraphMetadata.getNebulaSpaceList() == null || nebulaGraphMetadata.getNebulaSpaceList().isEmpty()) {
            dataSourceRootTreeNode.add(new PatchedDefaultMutableTreeNode("Not found any space"));
            return;
        }
        for (NebulaSpace nebulaSpace : nebulaGraphMetadata.getNebulaSpaceList()) {
            PatchedDefaultMutableTreeNode spaceTreeNode = getNebulaNode(nebulaSpace, dataSourceApi);
            addEdgeNode(dataSourceApi, nebulaSpace, spaceTreeNode);
            addTagNode(dataSourceApi, nebulaSpace, spaceTreeNode);
            dataSourceRootTreeNode.add(spaceTreeNode);
        }
    }

    private static void addEdgeNode(DataSourceApi dataSourceApi, NebulaSpace nebulaSpace, PatchedDefaultMutableTreeNode spaceTreeNode) {
        if (nebulaSpace.getEdgeList() != null && !nebulaSpace.getEdgeList().isEmpty()) {
            for (NebulaEdge nebulaEdge : nebulaSpace.getEdgeList()) {
                PatchedDefaultMutableTreeNode edgeTreeNode = getNebulaNode(nebulaEdge, dataSourceApi);
                List<NebulaField> properties = nebulaEdge.getProperties();
                if (properties != null && !properties.isEmpty()) {
                    for (NebulaField field : properties) {
                        edgeTreeNode.add(getNebulaNode(field, dataSourceApi));
                    }
                }
                spaceTreeNode.add(edgeTreeNode);
            }
        }
    }

    private static void addTagNode(DataSourceApi dataSourceApi, NebulaSpace nebulaSpace, PatchedDefaultMutableTreeNode spaceTreeNode) {
        if (nebulaSpace.getTagList() != null && !nebulaSpace.getTagList().isEmpty()) {
            for (NebulaTag nebulaTag : nebulaSpace.getTagList()) {
                PatchedDefaultMutableTreeNode tagTreeNode = getNebulaNode(nebulaTag, dataSourceApi);
                List<NebulaField> properties = nebulaTag.getProperties();
                if (properties != null && !properties.isEmpty()) {
                    for (NebulaField field : properties) {
                        tagTreeNode.add(getNebulaNode(field, dataSourceApi));
                    }
                }
                spaceTreeNode.add(tagTreeNode);
            }
        }
    }

    private static PatchedDefaultMutableTreeNode getNebulaNode(Object node, DataSourceApi dataSourceApi) {
        if (node instanceof NebulaSpace) {
            NebulaSpace nebulaSpace = (NebulaSpace) node;
            return new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.SPACE, dataSourceApi, nebulaSpace.getSpaceName(), GraphIcons.Nodes.NEBULA_SPACE, nebulaSpace));
        }
        if (node instanceof NebulaTag) {
            NebulaTag nebulaTag = (NebulaTag) node;
            return new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.TAG, dataSourceApi, nebulaTag.getTagName(), GraphIcons.Nodes.NEBULA_TAG, nebulaTag));
        }
        if (node instanceof NebulaEdge) {
            NebulaEdge nebulaEdge = (NebulaEdge) node;
            return new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.EDGE, dataSourceApi, nebulaEdge.getEdgeName(), GraphIcons.Nodes.NEBULA_EDGE, nebulaEdge));
        }
        if (node instanceof NebulaField) {
            NebulaField field = (NebulaField) node;
            return new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.PROP, dataSourceApi, field.getName(), getNebulaPropertyIcon(field.getType()), node));
        }
        throw new IllegalArgumentException("Unsupported nebula node type: " + node.getClass());
    }

    private static Icon getNebulaPropertyIcon(String propertyType) {
        switch (propertyType.toLowerCase()) {
            case "int8":
            case "int16":
            case "int32":
            case "int64":
            case "float":
            case "double":
                return GraphIcons.Nodes.NEBULA_FIELD_NUM;
            case "bool":
                return GraphIcons.Nodes.NEBULA_FIELD_BOOLEAN;
            case "date":
            case "time":
            case "datetime":
            case "timestamp":
            case "duration":
                return GraphIcons.Nodes.NEBULA_FIELD_TIME;
            case "string":
            case "fixed_string":
                return GraphIcons.Nodes.NEBULA_FIELD_STR;
            default:
                return GraphIcons.Nodes.NEBULA_FIELD;
        }
    }

    private PatchedDefaultMutableTreeNode of(MetadataTreeNodeModel model) {
        return new PatchedDefaultMutableTreeNode(model);
    }
}
