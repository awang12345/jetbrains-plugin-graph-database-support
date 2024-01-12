package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.table;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.table.JBTable;
import com.intellij.util.messages.MessageBus;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultRow;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.actions.execute.ExecuteQueryPayload;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.state.DataSourceApi;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.GraphConsoleView;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CopyQueryOutputEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.event.QueryExecutionProcessEvent;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.table.editor.CompositeTableCellEditor;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.table.renderer.CompositeTableCellRenderer;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.tree.TableContextMenuMouseAdapter;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.LogHelper;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.helpers.SerialisationHelper;
import icons.GraphIcons;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TablePanel {

    public static final int MAX_WIDTH = 300;
    private final ValueConverter valueConverter;
    private QueryResultTableModel tableModel;
    private JBTable table;

    private GraphQueryResult queryResult;

    private DataSourceApi dataSource;

    private JButton prePage;

    private JButton nextPage;

    private JLabel showPage;

    private ColumnResizer cr;

    private ComboBox<Integer> pageSizeComboBox;

    private Project project;

    public TablePanel() {
        valueConverter = new ValueConverter(this);
    }

    public void initialize(GraphConsoleView graphConsoleView, Project project) {
        this.project = project;
        MessageBus messageBus = project.getMessageBus();
        tableModel = new QueryResultTableModel();
        table = graphConsoleView.getTableExecuteResults();
        table.setModel(tableModel);

        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        table.setDefaultRenderer(Object.class, new CompositeTableCellRenderer());
        table.setDefaultEditor(Object.class, new CompositeTableCellEditor());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.addMouseListener(new TableContextMenuMouseAdapter());

        // bottom
        JPanel bottomToolbar = new JPanel(new BorderLayout());
        // page
        bottomToolbar.add(this.createPagePanel(), BorderLayout.CENTER);
        graphConsoleView.getTableTab().add(bottomToolbar, BorderLayout.SOUTH);

        cr = new ColumnResizer(table, MAX_WIDTH);

        messageBus.connect().subscribe(QueryExecutionProcessEvent.QUERY_EXECUTION_PROCESS_TOPIC, new QueryExecutionProcessEvent() {
            private DataSourceApi dataSourceApi;

            @Override
            public void executionStarted(DataSourceApi dataSource, ExecuteQueryPayload payload) {
                this.dataSourceApi = dataSource;
                TablePanel.this.dataSource = dataSource;
                showPage.setText(String.valueOf(0));
                tableModel.setColumnCount(0);
                tableModel.setRowCount(0);
            }

            @Override
            public void resultReceived(ExecuteQueryPayload payload, GraphQueryResult result) {
                TablePanel.this.queryResult = result;
                nextPage.doClick();
            }

            @Override
            public void postResultReceived(ExecuteQueryPayload payload) {
                cr.resize();
                updateRowHeights();
            }

            @Override
            public void handleError(ExecuteQueryPayload payload, Exception exception) {
            }

            @Override
            public void executionCompleted(ExecuteQueryPayload payload) {
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_C) { // Copy
                        StringSelection selection = new StringSelection(SerialisationHelper.convertToCsv(table, true));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                }
            }
        });

        messageBus.connect().subscribe(CopyQueryOutputEvent.COPY_QUERY_OUTPUT_TOPIC, () -> {
            JBTable tableToExport = graphConsoleView.getTableExecuteResults();
            StringSelection selection = new StringSelection(SerialisationHelper.convertToCsv(tableToExport, false));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

        });

    }

    public void updateRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = 1;
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    private JPanel createPagePanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        int page = 0;
        showPage = new JLabel(String.valueOf(page));
        JLabel showCount = new JLabel(String.format(" %s-%s of %s ", 0, 0, 0));

        prePage = new JButton(GraphIcons.Nodes.PRE_PAGE);
        prePage.setBorderPainted(false);
        prePage.setContentAreaFilled(false);
        prePage.addActionListener(new PageActionListener(showPage, showCount, false));

        nextPage = new JButton(GraphIcons.Nodes.NEXT_PAGE);
        nextPage.setBorderPainted(false);
        nextPage.setContentAreaFilled(false);
        nextPage.addActionListener(new PageActionListener(showPage, showCount, true));

        Integer[] pageSize = {10, 20, 50, 100, 500, 1000};
        pageSizeComboBox = new ComboBox<>(pageSize);
        pageSizeComboBox.setSelectedItem(100);
        pageSizeComboBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                showPage.setText(String.valueOf(0));
                nextPage.doClick();
            }
        });

        buttonPanel.add(new JLabel("Rows per page: "));
        buttonPanel.add(pageSizeComboBox);
        buttonPanel.add(showCount);

        buttonPanel.add(prePage);
        buttonPanel.add(showPage);
        buttonPanel.add(nextPage);

        return buttonPanel;
    }

    class PageActionListener implements ActionListener {
        private final JLabel showPage;
        private final JLabel showCount;
        private final boolean nextPage;

        public PageActionListener(JLabel showPage, JLabel showCount, boolean nextPage) {
            this.showPage = showPage;
            this.showCount = showCount;
            this.nextPage = nextPage;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GraphQueryResult result = TablePanel.this.queryResult;
            if (result == null) {
                return;
            }
            long oldPage = Long.parseLong(showPage.getText());
            long count = result.getRows().size();
            long page = calculatePage(count, oldPage, nextPage);

            int pageSize = (int) pageSizeComboBox.getSelectedItem();
            int startRow = (int) (Math.max(0, page - 1) * pageSize);
            int endRow = Math.min(startRow + pageSize, result.getRows().size());

            if (startRow >= endRow || startRow < 0) {
                return;
            }

            // update view
            tableModel.setColumnCount(0);
            tableModel.setRowCount(0);

            List<GraphQueryResultColumn> columns = result.getColumns();
            tableModel.addColumn("#");
            columns.forEach((column) -> tableModel.addColumn(column.getName()));
            int seq = 0;

            for (int i = startRow; i < endRow; i++) {
                List<Object> data = new ArrayList<>(columns.size());
                data.add(seq++);
                GraphQueryResultRow row = result.getRows().get(i);
                columns.forEach((column) -> data.add(valueConverter.convert(column.getName(), row.getValue(column), TablePanel.this.dataSource)));
                tableModel.addRow(data.toArray());
            }

            // update page
            showPage.setText(String.valueOf(page));
            showCount.setText(String.format(" %s-%s of %s ", startRow, endRow, count));

            cr.resize();
            updateRowHeights();
        }

        private long calculatePage(long count, long oldPage, boolean nextPage) {
            int pageSize = (int) pageSizeComboBox.getSelectedItem();
            long countPage = (count % pageSize == 0) ? count / pageSize : (count / pageSize) + 1;
            return nextPage ? Math.min(oldPage + 1, countPage) : Math.max(oldPage - 1, 1);
        }
    }
}
