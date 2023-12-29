package com.neueda.jetbrains.plugin.graphdb.database.nebula.data;

import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphPropertyContainer;

import java.util.*;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 15:56
 */
public class NebulaGraphNode implements GraphNode {

    private String id;
    private String name;
    private Map<String, Object> properties;

    public NebulaGraphNode(String id) {
        this.id = id;
        this.name = id;
    }

    public NebulaGraphNode(String id, String name, Map<String, Object> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public GraphPropertyContainer getPropertyContainer() {
        return () -> Optional.ofNullable(this.properties).orElse(Collections.emptyMap());
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList(this.name);
    }

    @Override
    public String getTypesName() {
        return this.name;
    }

    @Override
    public boolean isTypesSingle() {
        return false;
    }
}
