package com.neueda.jetbrains.plugin.graphdb.database.nebula.query;

import com.neueda.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:05
 */
public class NebulaGraphQueryResultColumn implements GraphQueryResultColumn {

    private String name;

    public NebulaGraphQueryResultColumn(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
