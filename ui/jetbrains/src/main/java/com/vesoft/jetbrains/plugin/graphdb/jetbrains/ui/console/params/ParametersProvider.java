package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.params;

public interface ParametersProvider {

    String getGlobalParametersJson();

    String getFileSpecificParametersJson();

}
