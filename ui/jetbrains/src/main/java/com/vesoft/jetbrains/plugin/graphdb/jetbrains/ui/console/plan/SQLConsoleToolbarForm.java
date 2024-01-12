package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.DataSourceHelper;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.LogHelper;
import icons.GraphIcons;
import org.apache.commons.lang.StringUtils;
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

        DataSourceHelper.getDataSourceApi(fileEditor.getFile().getName(), project).ifPresent(dataSourceApi -> {
            SQLConsoleSpaceSelectAction spaceSelectAction =
                    (SQLConsoleSpaceSelectAction) actionManager.getAction(SQLConsoleSpaceSelectAction.ID);
            String currentSpace = DataContext.getInstance(project).getCurrentSpace(dataSourceApi);
            if (StringUtils.isNotBlank(currentSpace)) {
                spaceSelectAction.getTemplatePresentation().setText(currentSpace, false);
            }
        });
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
