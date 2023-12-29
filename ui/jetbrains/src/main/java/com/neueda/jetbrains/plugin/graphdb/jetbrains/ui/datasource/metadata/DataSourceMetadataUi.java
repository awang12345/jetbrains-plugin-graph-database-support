package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphMetadata;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourcesComponentMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.Neo4jBoltCypherDataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.*;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.MutableTreeNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType.*;
import static com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.Neo4jTreeNodeType.*;
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

    public CompletableFuture<Boolean> updateDataSourceMetadataUi(PatchedDefaultMutableTreeNode node, DataSourceApi nodeDataSource) {
        DataSourceType sourceType = nodeDataSource.getDataSourceType();
        if (handlers.containsKey(sourceType)) {
            return dataSourcesComponent.getMetadata(nodeDataSource)
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
            PatchedDefaultMutableTreeNode spaceTreeNode = new PatchedDefaultMutableTreeNode(
                    new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.SPACE, dataSourceApi,
                            String.format("Space:(%s)", nebulaSpace.getSpaceName()), GraphIcons.Nodes.CONSTRAINT));
            addEdgeNode(dataSourceApi, nebulaSpace, spaceTreeNode);
            addTagNode(dataSourceApi, nebulaSpace, spaceTreeNode);
            dataSourceRootTreeNode.add(spaceTreeNode);
        }
    }

    private static void addEdgeNode(DataSourceApi dataSourceApi, NebulaSpace nebulaSpace, PatchedDefaultMutableTreeNode spaceTreeNode) {
        if (nebulaSpace.getEdgeList() != null && !nebulaSpace.getEdgeList().isEmpty()) {
            for (NebulaEdge nebulaEdge : nebulaSpace.getEdgeList()) {
                PatchedDefaultMutableTreeNode edgeTreeNode
                        = new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.EDGE, dataSourceApi, nebulaEdge.getTagName()));
                if (nebulaEdge.getProperties() != null && !nebulaEdge.getProperties().isEmpty()) {
                    for (Map.Entry<String, String> entry : nebulaEdge.getProperties().entrySet()) {
                        PatchedDefaultMutableTreeNode propTreeNode = new PatchedDefaultMutableTreeNode(
                                new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.PROP, dataSourceApi, String.format("%s (%s)", entry.getKey(), entry.getValue())));
                        edgeTreeNode.add(propTreeNode);
                    }
                }
                spaceTreeNode.add(edgeTreeNode);
            }
        }
    }

    private static void addTagNode(DataSourceApi dataSourceApi, NebulaSpace nebulaSpace, PatchedDefaultMutableTreeNode spaceTreeNode) {
        if (nebulaSpace.getTagList() != null && !nebulaSpace.getTagList().isEmpty()) {
            for (NebulaTag nebulaTag : nebulaSpace.getTagList()) {
                PatchedDefaultMutableTreeNode tagTreeNode
                        = new PatchedDefaultMutableTreeNode(new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.TAG, dataSourceApi, nebulaTag.getTagName()));
                if (nebulaTag.getProperties() != null && !nebulaTag.getProperties().isEmpty()) {
                    for (Map.Entry<String, String> entry : nebulaTag.getProperties().entrySet()) {
                        PatchedDefaultMutableTreeNode propTreeNode = new PatchedDefaultMutableTreeNode(
                                new NebulaMetadataTreeNodeModel(NebulaTreeNodeType.PROP, dataSourceApi, String.format("%s (%s)", entry.getKey(), entry.getValue())));
                        tagTreeNode.add(propTreeNode);
                    }
                }
                spaceTreeNode.add(tagTreeNode);
            }
        }
    }

    private PatchedDefaultMutableTreeNode of(MetadataTreeNodeModel model) {
        return new PatchedDefaultMutableTreeNode(model);
    }
}
