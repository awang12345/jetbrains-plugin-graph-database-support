package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.util.messages.Topic;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;

public interface MetadataRefreshEvent {

    Topic<MetadataRefreshEvent> METADATA_REFRESH_EVENT = Topic.create("GraphDatabaseDataSource.MetadataRefresh", MetadataRefreshEvent.class);

    default void startMetadataRefresh(DataSourceApi nodeDataSource){};
}
