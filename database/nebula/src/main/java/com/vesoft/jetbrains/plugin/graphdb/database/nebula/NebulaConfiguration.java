package com.vesoft.jetbrains.plugin.graphdb.database.nebula;

import java.util.Map;

public class NebulaConfiguration {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DEFAULT_SPACE = "defaultSpace";

    private final Map<String, String> configuration;

    public NebulaConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public String getHost() {
        return configuration.get(HOST);
    }

    public Integer getPort() {
        return Integer.valueOf(configuration.getOrDefault(PORT, "7687"));
    }

    public String getUser() {
        return configuration.get(USER);
    }

    public String getPassword() {
        return configuration.get(PASSWORD);
    }

    public String getDefaultSpace() {
        return configuration.get(DEFAULT_SPACE);
    }
}
