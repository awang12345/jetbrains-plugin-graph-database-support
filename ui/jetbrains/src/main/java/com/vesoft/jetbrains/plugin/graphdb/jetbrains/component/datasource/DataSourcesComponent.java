package com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourcesComponentMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceContainer;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourcesComponentState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "GraphDatabaseSupport.DataSourcesState",
        storages = {@Storage("GraphDatabaseSupport_DataSourcesState.xml")})
public class DataSourcesComponent implements ProjectComponent, PersistentStateComponent<DataSourcesComponentState> {

    private DataSourcesComponentState state;

    public DataSourceContainer getDataSourceContainer() {
        state.migrate();
        return state.getCurrentContainer();
    }

    /**
     * Load persisted state.
     */
    @Override
    public void loadState(DataSourcesComponentState state) {
        this.state = state;
    }

    /**
     * Get state for persisting.
     */
    @Nullable
    @Override
    public DataSourcesComponentState getState() {
        return state;
    }

    /**
     * Initialization.
     */
    @Override
    public void initComponent() {
        if (state == null) {
            state = new DataSourcesComponentState();
        }
    }

    public void refreshAllMetadata() {
        throw new UnsupportedOperationException("Not implemented method 'refreshAllMetadata'  yet");
    }

    /**
     * Project is opened.
     */
    @Override
    public void projectOpened() {
    }

    /**
     * Project is closing.
     */
    @Override
    public void projectClosed() {
    }

    /**
     * Clean up component.
     */
    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GraphDatabaseSupport.DataSources";
    }
}
