package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:46
 */
public class NebulaTag extends NebulaSchema {


    private String tagName;

    private Map<String, String> properties;


    public NebulaTag(String space, String tagName, Map<String, String> properties, String ddl) {
        super(space, ddl);
        this.tagName = tagName;
        this.properties = properties;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

}
