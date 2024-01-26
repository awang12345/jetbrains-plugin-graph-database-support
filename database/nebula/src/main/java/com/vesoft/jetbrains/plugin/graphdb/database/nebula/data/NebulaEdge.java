package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.List;
import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:46
 */
public class NebulaEdge extends NebulaSchema {


    private String edgeName;

    private List<NebulaField> fieldList;


    public NebulaEdge(String spaceName, String edgeName, List<NebulaField> fieldList, String ddl) {
        super(spaceName, ddl);
        this.edgeName = edgeName;
        this.fieldList = fieldList;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public List<NebulaField> getProperties() {
        return fieldList;
    }

}
