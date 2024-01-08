package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import java.util.Map;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:46
 */
public class NebulaTag {

    private String tagName;

    private Map<String, String> properties;

    private String ddl;

    public NebulaTag(String tagName, Map<String, String> properties, String ddl) {
        this.tagName = tagName;
        this.properties = properties;
        this.ddl = ddl;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getDdl() {
        return this.ddl;
    }
}
