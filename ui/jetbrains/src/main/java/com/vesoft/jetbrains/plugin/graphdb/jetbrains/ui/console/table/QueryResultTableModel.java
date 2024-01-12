package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.table;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.table.DefaultTableModel;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class QueryResultTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        Object valueAt = getValueAt(row, column);
        return valueAt != null && valueAt instanceof Tree;
    }
}
