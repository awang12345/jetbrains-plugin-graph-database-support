package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.List;
import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:46
 */
public class NebulaTag extends NebulaSchema {


    private String tagName;

    private List<NebulaField> fieldList;


    public NebulaTag(String space, String tagName, List<NebulaField> fieldList, String ddl) {
        super(space, ddl);
        this.tagName = tagName;
        this.fieldList = fieldList;
    }

    public String getTagName() {
        return tagName;
    }

    public List<NebulaField> getProperties() {
        return fieldList;
    }

}
