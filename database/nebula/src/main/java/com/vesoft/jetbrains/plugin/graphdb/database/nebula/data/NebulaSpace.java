package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.List;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:45
 */
public class NebulaSpace extends NebulaSchema {

    private String type;

    private List<NebulaEdge> edgeList;
    private List<NebulaTag> tagList;


    public NebulaSpace(String spaceName, List<NebulaEdge> edgeList, List<NebulaTag> tagList, String ddl) {
        super(spaceName, ddl);
        this.edgeList = edgeList;
        this.tagList = tagList;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<NebulaEdge> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<NebulaEdge> edgeList) {
        this.edgeList = edgeList;
    }

    public List<NebulaTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<NebulaTag> tagList) {
        this.tagList = tagList;
    }

}
