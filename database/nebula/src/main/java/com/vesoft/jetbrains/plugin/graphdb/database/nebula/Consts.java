package com.vesoft.jetbrains.plugin.graphdb.database.nebula;


public interface Consts {

    interface Stetments {
        String SHOW_SPACE = "SHOW SPACES";
        String USE_SPACE = "USE %s";
        String SHOW_EDGES = "SHOW EDGES";
        String SHOW_TAGS = "SHOW TAGS";
        String DESC_TAG = "DESCRIBE TAG %s";
        String DESC_EDGE = "DESCRIBE EDGE %s";

        String SHOW_CREATE_EDGE = "SHOW CREATE EDGE %s";
        String SHOW_CREATE_TAG = "SHOW CREATE TAG %s";
        String SHOW_CREATE_SPACE = "SHOW CREATE SPACE %s";

    }

}
