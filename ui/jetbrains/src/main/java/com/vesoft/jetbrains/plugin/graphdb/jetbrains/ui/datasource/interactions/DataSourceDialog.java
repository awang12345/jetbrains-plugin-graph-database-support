package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.interactions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.database.DatabaseManagerService;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.services.ExecutorService;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.DataSourcesView;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.vesoft.jetbrains.plugin.graphdb.database.opencypher.gremlin.exceptions.ExceptionWrapper.getCause;

public abstract class DataSourceDialog extends DialogWrapper {
    public static final int THICKNESS = 10;
    public static final int HEIGHT = 150;

    private final ValidationCallback defaultValidationCallback = new ValidationCallback() {

        @Override
        public void onSuccess(JPanel popupPanel, JComponent contentPanel) {
            connectionSuccessful(popupPanel, contentPanel);
        }

        @Override
        public void onFailure(JPanel popupPanel, JComponent contentPanel, Exception ex) {
            connectionFailed(ex, popupPanel, contentPanel);
        }
    };

    protected DataSourceDialog(@Nullable Project project, DataSourcesView dataSourcesView) {
        super(project);
        Disposer.register(project, myDisposable);
        init();
    }

    public abstract DataSourceApi constructDataSource();

    protected abstract void showLoading();

    protected abstract void hideLoading();

    public boolean go() {
        init();
        return showAndGet();
    }

    public void validationPopup() {
        validationPopup(defaultValidationCallback);
    }

    public void validationPopup(ValidationCallback validationCallback) {
        JPanel popupPanel = new JPanel(new BorderLayout());
        popupPanel.setBorder(JBUI.Borders.empty(THICKNESS));

        ValidationInfo validationInfo = doValidate();
        if (validationInfo != null) {
            JLabel connectionFailed = new JLabel("Connection failed: " + validationInfo.message,
                    GraphIcons.Nodes.PROCESS_FAIL, JLabel.LEFT);
            popupPanel.add(connectionFailed, BorderLayout.CENTER);
            createPopup(popupPanel, getContentPanel());
            validationCallback.onFailure(popupPanel, getContentPanel(), new Exception(validationInfo.message));
        } else {
            validateConnection(popupPanel, getContentPanel(), validationCallback);
        }
    }

    private void createPopup(JPanel popupPanel, JComponent contentPanel) {
        if (contentPanel.isShowing()) {
            JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(popupPanel, getPreferredFocusedComponent())
                    .setCancelButton(new IconButton("Close", AllIcons.Actions.Close))
                    .setTitle("Test connection")
                    .setResizable(true)
                    .setMovable(true)
                    .setCancelButton(new IconButton("Close", AllIcons.Actions.Close, AllIcons.Actions.CloseHovered))
                    .createPopup()
                    .showInCenterOf(contentPanel);
        }
    }

    private void validateConnection(
            JPanel popupPanel,
            JComponent contentPanel, ValidationCallback validationCallback) {
        ExecutorService executorService = ServiceManager.getService(ExecutorService.class);
        showLoading();
        executorService.runInBackground(
                this::executeOkQuery,
                (status) -> validationCallback.onSuccess(popupPanel, contentPanel),
                (exception) -> validationCallback.onFailure(popupPanel, contentPanel, exception),
                ModalityState.current()
        );
    }

    private String executeOkQuery() {
        DataSourceApi dataSource = constructDataSource();
        DatabaseManagerService databaseManager = ServiceManager.getService(DatabaseManagerService.class);
        GraphDatabaseApi db = databaseManager.getDatabaseFor(dataSource);
        return testQuery(db);
    }

    @NotNull
    protected String testQuery(GraphDatabaseApi db) {
        GraphQueryResult result = db.execute("RETURN 'ok'");

        Object value = result.getRows().get(0).getValue(result.getColumns().get(0));

        if (value.equals("ok")) {
            return "ok";
        } else {
            throw new RuntimeException("Unexpected test query output: " + value);
        }
    }

    private void connectionSuccessful(
            JPanel popupPanel,
            JComponent contentPanel) {
        hideLoading();
        JLabel connectionSuccessful = new JLabel("Connection successful!", GraphIcons.Nodes.PROCESS_OK, JLabel.LEFT);
        popupPanel.add(connectionSuccessful, BorderLayout.CENTER);

        createPopup(popupPanel, contentPanel);
    }

    private void connectionFailed(
            Exception exception,
            JPanel popupPanel,
            JComponent contentPanel) {
        hideLoading();

        JLabel connectionFailed = new JLabel("Connection failed: " +
                exception.getMessage(), GraphIcons.Nodes.PROCESS_FAIL, JLabel.LEFT);

        JTextArea exceptionCauses = new JTextArea();
        exceptionCauses.setLineWrap(false);
        exceptionCauses.append(getCause(exception));

        JBScrollPane scrollPane = new JBScrollPane(exceptionCauses);
        scrollPane.setPreferredSize(new Dimension(-1, HEIGHT));
        popupPanel.add(connectionFailed, BorderLayout.NORTH);
        popupPanel.add(scrollPane, BorderLayout.CENTER);

        createPopup(popupPanel, contentPanel);
    }

    @Override
    protected void doOKAction() {
        validationPopup(new ValidationCallback() {
            @Override
            public void onSuccess(JPanel popupPanel, JComponent contentPanel) {
                DataSourceDialog.super.doOKAction();
            }

            @Override
            public void onFailure(JPanel popupPanel, JComponent contentPanel, Exception ex) {
                connectionFailed(ex, popupPanel, contentPanel);
            }
        });
    }

    interface ValidationCallback {
        void onSuccess(JPanel popupPanel, JComponent contentPanel);

        void onFailure(JPanel popupPanel, JComponent contentPanel, Exception ex);
    }

}
