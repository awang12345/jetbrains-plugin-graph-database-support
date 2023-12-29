package com.neueda.jetbrains.plugin.graphdb.database.nebula;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 16:49
 */
public interface Consts {

    interface Stetments {
        String SHOW_SPACE = "SHOW SPACES";
        String USE_SPACE = "USE %s";
        String SHOW_EDGES = "SHOW EDGES";
        String SHOW_TAGS = "SHOW TAGS";
        String DESC_TAG = "DESCRIBE TAG %s";
        String DESC_EDGE = "DESCRIBE EDGE %s";

    }

}
