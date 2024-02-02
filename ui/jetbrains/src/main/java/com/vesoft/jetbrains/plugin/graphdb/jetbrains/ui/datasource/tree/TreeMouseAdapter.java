package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSchema;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.analytics.Analytics;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.LogHelper;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.FileUtil;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.Notifier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Optional;

import static com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.UiHelper.cast;

public class TreeMouseAdapter extends MouseAdapter {

    private ContextMenuService contextMenuService = new ContextMenuService();

    private Project project;

    public TreeMouseAdapter() {
    }

    public TreeMouseAdapter(Project project) {
        this.project = project;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Tree tree = (Tree) e.getComponent();
        TreePath pathForLocation = tree.getPathForLocation(e.getX(), e.getY());
        int clickCount = e.getClickCount();
        if (clickCount == 2 && project != null) {
            DefaultMutableTreeNode[] selectedNodes = tree.getSelectedNodes(DefaultMutableTreeNode.class, this::isNebulaNode);
            if (selectedNodes.length != 1) {
                return;
            }
            DefaultMutableTreeNode selectedNode = selectedNodes[0];
            NebulaMetadataTreeNodeModel treeNodeModelApi = (NebulaMetadataTreeNodeModel) selectedNode.getUserObject();
            DataSourceApi dataSource = treeNodeModelApi.getDataSourceApi();
            NebulaSchema schema = treeNodeModelApi.getSchema();
            String space = schema.getSpaceName();
            String nGql = "USE " + space + ";";
            if (schema instanceof NebulaTag) {
                nGql += String.format("MATCH (v:%s) RETURN v LIMIT 20;", ((NebulaTag) schema).getTagName());
            } else if (schema instanceof NebulaEdge) {
                nGql += String.format("MATCH (a)-[e:%s]->(b) RETURN a,b,e LIMIT 20;", ((NebulaEdge) schema).getEdgeName());
            }
            Analytics.event(dataSource, "openEditor");
            try {
                FileUtil.openFile(project, FileUtil.getDataSourceFile(project, dataSource));
                project.getMessageBus().syncPublisher(ExecuteQueryEvent.EXECUTE_QUERY_TOPIC)
                        .executeQuery(dataSource, new ExecuteQueryPayload(nGql));
            } catch (IOException exception) {
                Notifier.error("Open editor error", exception.getMessage());
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            Optional.ofNullable(pathForLocation)
                    .flatMap(p -> cast(p.getLastPathComponent(), PatchedDefaultMutableTreeNode.class))
                    .flatMap(n -> cast(n.getUserObject(), LinkLabel.class))
                    .ifPresent(LinkLabel::doClick);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            DataContext dataContext = DataManager.getInstance().getDataContext(tree);
            contextMenuService.getContextMenu(pathForLocation)
                    .ifPresent(dto -> dto.showPopup(dataContext));
        }
    }


    private boolean isNebulaNode(DefaultMutableTreeNode node) {
        if (!(node.getUserObject() instanceof NebulaMetadataTreeNodeModel)) {
            return false;
        }
        return ((NebulaMetadataTreeNodeModel) node.getUserObject()).getSchema() != null;
    }
}
