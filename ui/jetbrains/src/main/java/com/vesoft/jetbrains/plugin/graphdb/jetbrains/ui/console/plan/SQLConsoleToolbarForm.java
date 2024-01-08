package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SQLConsoleToolbarForm implements Disposable {
    private final JPanel actionsPanel;

    public SQLConsoleToolbarForm(Project project, FileEditor fileEditor) {
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = (DefaultActionGroup) actionManager.getAction("NebulaGraph.ActionGroup.SQLFileEditor");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("bar", actionGroup, true);
        actionToolbar.setTargetComponent(actionsPanel);
        actionToolbar.setOrientation(SwingConstants.HORIZONTAL);

        JLabel sqlLabel = new JLabel("Nebula-Graph");
        sqlLabel.setIcon(GraphIcons.Database.NEBULA);
        actionsPanel.add(sqlLabel);

        // actionToolbar
        actionsPanel.add(actionToolbar.getComponent());

        JLabel tipsLabel = new JLabel("Ctrl + Enter  - Execute select nGQL");
        actionsPanel.add(tipsLabel);
    }

    @NotNull
    public JPanel getMainComponent() {
        return this.actionsPanel;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }
}
