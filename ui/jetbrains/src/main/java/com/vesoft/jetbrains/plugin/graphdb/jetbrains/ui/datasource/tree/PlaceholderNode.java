package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree;

import org.apache.commons.lang3.StringUtils;

import javax.swing.tree.DefaultMutableTreeNode;

public class PlaceholderNode extends DefaultMutableTreeNode {

    public static final PlaceholderNode LOADING = new PlaceholderNode("Loading...", Type.LOADING);

    public enum Type {
        LOADING,
    }

    private String text;

    private Type type;

    public PlaceholderNode(String text, Type type) {
        super(text);
        this.text = text;
        this.type = type;
    }

    @Override
    public String toString() {
        return StringUtils.defaultString(text);
    }

    public boolean loading() {
        return type == Type.LOADING;
    }
}
