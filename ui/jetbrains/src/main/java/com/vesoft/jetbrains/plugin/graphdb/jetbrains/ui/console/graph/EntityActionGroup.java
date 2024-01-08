package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.graph;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.NoIdGraphEntity;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.model.ObjectModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityActionGroup extends ActionGroup {

    private AnAction[] actions;

    public EntityActionGroup(DataSourceApi dataSourceApi, NoIdGraphEntity entity) {
        if (entity instanceof GraphNode) {
            actions = new AnAction[]{
                    new CopyToClipboardAction("Copy node", "", null, entity),
                    new NodeEditAction("Edit node", "", null, dataSourceApi, (GraphNode) entity),
                    new NodeDeleteAction("Delete node", "", null, dataSourceApi, (GraphNode) entity),
            };
        } else if (entity instanceof GraphRelationship) {
            actions = new AnAction[]{
                    new CopyToClipboardAction("Copy relationship", "", null, entity),
                    new RelationshipEditAction("Edit relationship", "", null, dataSourceApi, (GraphRelationship) entity),
                    new RelationshipDeleteAction("Delete relationship", "", null, dataSourceApi, (GraphRelationship) entity)
            };
        } else if (entity instanceof ObjectModel) {
            actions = new AnAction[] {
                    new CopyToClipboardAction("Copy " + entity.getRepresentation().toLowerCase(), "", null, entity),
                    new CopyToClipboardAction("Copy value", "", null, ((ObjectModel) entity).getValue().orElse(null))
            };
        } else {
            actions = new AnAction[]{
                    new CopyToClipboardAction("Copy " + entity.getRepresentation().toLowerCase(), "", null, entity)
            };
        }
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return actions;
    }
}
