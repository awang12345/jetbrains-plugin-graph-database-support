package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.NebulaTreeNodeType;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.Neo4jTreeNodeType;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.NodeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sourcecode.Impls;

import static icons.GraphIcons.Database.NEO4J;

public class MetadataActionGroup extends ActionGroup {

    private final NodeType type;
    private final String data;
    private final String dataSourceUuid;

    private final Object extData;

    public MetadataActionGroup(NodeType type, String data, String dataSourceUuid, Object extData) {
        this.type = type;
        this.data = data;
        this.dataSourceUuid = dataSourceUuid;
        this.extData = extData;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        if (type == Neo4jTreeNodeType.RELATIONSHIP) {
            return new AnAction[]{new MetadataRelationshipAction(data, dataSourceUuid, "Query this relationship", "", NEO4J)};
        } else if (type == Neo4jTreeNodeType.LABEL) {
            return new AnAction[]{
                    new MetadataLabelAction(data, dataSourceUuid, "Nodes with this label", "", NEO4J),
                    new MetadataLabelFromAction(data, dataSourceUuid, "Outgoing relationships", "", NEO4J),
                    new MetadataLabelToAction(data, dataSourceUuid, "Incoming relationships", "", NEO4J)
            };
        } else if (type == Neo4jTreeNodeType.PROPERTY_KEY) {
            return new AnAction[]{new MetadataPropertyKeyAction(data, dataSourceUuid, "Query this property", "", NEO4J)};
        } else if (type == NebulaTreeNodeType.SPACE) {
            return new AnAction[]{new ShowNebulaDDLAction(extData)};
        } else if (type == NebulaTreeNodeType.EDGE) {
            return new AnAction[]{new ShowNebulaDDLAction(extData)};
        } else if (type == NebulaTreeNodeType.TAG) {
            return new AnAction[]{new ShowNebulaDDLAction(extData)};
        } else {
            return new AnAction[]{};
        }
    }
}
