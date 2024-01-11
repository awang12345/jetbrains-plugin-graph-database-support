package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:46
 */
public class NebulaEdge extends NebulaSchema {


    private String edgeName;

    private Map<String, String> properties;


    public NebulaEdge(String spaceName, String edgeName, Map<String, String> properties, String ddl) {
        super(spaceName, ddl);
        this.edgeName = edgeName;
        this.properties = properties;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

}
