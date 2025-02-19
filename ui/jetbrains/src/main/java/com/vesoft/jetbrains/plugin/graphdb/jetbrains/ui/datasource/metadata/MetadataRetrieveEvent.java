package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.util.messages.Topic;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;

public interface MetadataRetrieveEvent {

    Topic<MetadataRetrieveEvent> METADATA_RETRIEVE_EVENT = Topic.create("GraphDatabaseDataSource.MetadataRetrieve", MetadataRetrieveEvent.class);

    default void startMetadataRefresh(DataSourceApi nodeDataSource){};

    default void metadataRefreshSucceed(DataSourceApi nodeDataSource, DataSourceMetadata metadata){};

    default void metadataRefreshFailed(DataSourceApi nodeDataSource, Exception exception){};
}
