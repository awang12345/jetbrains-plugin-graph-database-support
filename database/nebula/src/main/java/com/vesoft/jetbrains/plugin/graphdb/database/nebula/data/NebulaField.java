package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/25 14:41
 */
public class NebulaField {

    private String name;
    private String type;
    private String comment;

    public NebulaField(String name, String type, String comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
    }

    public NebulaField() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
