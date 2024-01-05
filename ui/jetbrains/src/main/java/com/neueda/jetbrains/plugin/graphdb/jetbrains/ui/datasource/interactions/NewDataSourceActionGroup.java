package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.DataSourcesView;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions.nebula.NebulaDataSourceDialog;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions.neo4j.bolt.Neo4jBoltDataSourceDialog;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewDataSourceActionGroup extends ActionGroup {

    private NewDataSourceAction neo4jBoltDataSource;

    private NewDataSourceAction nebulaDataSource;

    public NewDataSourceActionGroup(Project project, DataSourcesView dataSourcesView) {
        nebulaDataSource = new NewDataSourceAction(
                dataSourcesView, new NebulaDataSourceDialog(project, dataSourcesView),
                "Nebula-Graph", "Connect to Nebula-Graph Data Source", GraphIcons.Database.NEBULA);
        neo4jBoltDataSource = new NewDataSourceAction(
                dataSourcesView, new Neo4jBoltDataSourceDialog(project, dataSourcesView),
                "Neo4j - Bolt", "Create Neo4j 3.0+ Bolt data source", GraphIcons.Database.NEO4J);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{nebulaDataSource, neo4jBoltDataSource};
    }
}
