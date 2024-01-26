package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/11 14:42
 */
public class NebulaSchema {

    private String id;
    private String spaceName;

    private String ddl;

    private String comment;

    private Long dataCount;

    public NebulaSchema(String id, String spaceName, String ddl) {
        this.id = id;
        this.spaceName = spaceName;
        this.ddl = ddl;
    }

    public NebulaSchema(String spaceName, String ddl) {
        this.spaceName = spaceName;
        this.ddl = ddl;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getDdl() {
        return ddl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }
}
