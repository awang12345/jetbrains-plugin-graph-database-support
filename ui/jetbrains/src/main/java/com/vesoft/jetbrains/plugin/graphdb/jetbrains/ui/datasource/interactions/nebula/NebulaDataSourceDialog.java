package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions.nebula;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.AsyncProcessIcon;
import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.NebulaConfiguration;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourceType;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.DataSourcesComponent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.DataSourcesView;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions.DataSourceDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.Validation.validation;

public class NebulaDataSourceDialog extends DataSourceDialog {
    private final DataSourcesComponent dataSourcesComponent;
    private DataSourceApi dataSourceToEdit;

    private Data data;

    private JPanel content;
    private JBTextField dataSourceNameField;
    private JBTextField hostField;
    private JBTextField userField;
    private JBPasswordField passwordField;
    private JBTextField portField;
    private JButton testConnectionButton;
    private JPanel loadingPanel;
    private AsyncProcessIcon loadingIcon;
    private JBTextField defaultSpaceField;

    public NebulaDataSourceDialog(Project project, DataSourcesView dataSourcesView, DataSourceApi dataSourceToEdit) {
        this(project, dataSourcesView);
        this.dataSourceToEdit = dataSourceToEdit;
    }

    public NebulaDataSourceDialog(Project project, DataSourcesView dataSourcesView) {
        super(project, dataSourcesView);
        loadingPanel.setVisible(false);
        dataSourcesComponent = dataSourcesView.getComponent();
        testConnectionButton.addActionListener(e -> this.validationPopup());
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(dataSourceNameField.getText())) {
            return validation("Data source name must not be empty", dataSourceNameField);
        }
        if (StringUtils.isBlank(hostField.getText())) {
            return validation("Host must not be empty", hostField);
        }
        if (!StringUtils.isNumeric(portField.getText())) {
            return validation("Port must be numeric", portField);
        }

        extractData();

        if (dataSourcesComponent.getDataSourceContainer().isDataSourceExists(data.dataSourceName)) {
            if (!(dataSourceToEdit != null && dataSourceToEdit.getName().equals(data.dataSourceName))) {
                return validation(String.format("Data source [%s] already exists", data.dataSourceName), dataSourceNameField);
            }
        }

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (dataSourceToEdit != null) {
            Map<String, String> conf = dataSourceToEdit.getConfiguration();
            String host = conf.get(NebulaConfiguration.HOST);
            String port = conf.get(NebulaConfiguration.PORT);
            String user = conf.get(NebulaConfiguration.USER);
            String password = conf.get(NebulaConfiguration.PASSWORD);
            String defaultSpace = conf.get(NebulaConfiguration.DEFAULT_SPACE);

            dataSourceNameField.setText(dataSourceToEdit.getName());
            hostField.setText(host);
            portField.setText(port);
            userField.setText(user);
            passwordField.setText(password);
            defaultSpaceField.setText(defaultSpace);
        }
        return content;
    }

    @Override
    public DataSourceApi constructDataSource() {
        extractData();

        Map<String, String> configuration = new HashMap<>();
        configuration.put(NebulaConfiguration.HOST, data.graphHost);
        configuration.put(NebulaConfiguration.PORT, data.graphPort);
        configuration.put(NebulaConfiguration.USER, data.user);
        configuration.put(NebulaConfiguration.PASSWORD, data.password);
        configuration.put(NebulaConfiguration.DEFAULT_SPACE, data.defaultSpace);

        return dataSourcesComponent.getDataSourceContainer().createDataSource(
                dataSourceToEdit,
                DataSourceType.NEBULA,
                data.dataSourceName,
                configuration
        );
    }

    @NotNull
    @Override
    protected String testQuery(GraphDatabaseApi db) {
        GraphQueryResult result = db.execute("yield 1");
        if (result.getRows().isEmpty()) {
            throw new IllegalStateException("Connect nebula graph fail!!!");
        }
        return "ok";
    }

    @Override
    protected void showLoading() {
        testConnectionButton.setEnabled(false);
        loadingIcon.resume();
        loadingPanel.setVisible(true);
    }

    @Override
    protected void hideLoading() {
        testConnectionButton.setEnabled(true);
        loadingIcon.suspend();
        loadingPanel.setVisible(false);
    }

    private void extractData() {
        data = new Data();
        data.dataSourceName = dataSourceNameField.getText();
        data.graphHost = hostField.getText();
        data.graphPort = portField.getText();
        data.user = userField.getText();
        data.password = String.valueOf(passwordField.getPassword()); // TODO: use password API
        data.defaultSpace = defaultSpaceField.getText();
    }

    private void createUIComponents() {
        loadingIcon = new AsyncProcessIcon("validateConnectionIcon");
    }

    private static final class Data {
        private String dataSourceName;
        private String graphHost;
        private String graphPort;
        private String user;
        private String password;
        private String defaultSpace;
    }
}
