package com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata;

import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphMetadata;

import java.util.List;
import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 17:00
 */
public class NebulaDataSourceMetadata implements DataSourceMetadata{

    private NebulaGraphMetadata nebulaGraphMetadata;

    public NebulaDataSourceMetadata(NebulaGraphMetadata nebulaGraphMetadata) {
        this.nebulaGraphMetadata = nebulaGraphMetadata;
    }

    public NebulaGraphMetadata getNebulaGraphMetadata() {
        return nebulaGraphMetadata;
    }

    @Override
    public List<Map<String, String>> getMetadata(String metadataKey) {
        return null;
    }

    @Override
    public boolean isMetadataExists(String metadataKey) {
        return false;
    }
}
