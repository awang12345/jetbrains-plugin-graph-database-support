package com.neueda.jetbrains.plugin.graphdb.database.nebula.query;

import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.neueda.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.neueda.jetbrains.plugin.graphdb.database.api.query.*;
import com.vesoft.nebula.client.graph.data.ResultSet;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 14:22
 */
public class NebulaGraphQueryResult implements GraphQueryResult {

    private long costTime;
    private ResultSet resultSet;
    private Exception exception;

    public NebulaGraphQueryResult(long startTime, ResultSet resultSet, Exception exception) {
        this.costTime = System.currentTimeMillis() - startTime;
        this.resultSet = resultSet;
        this.exception = exception;
    }

    @Override
    public long getExecutionTimeMs() {
        return this.costTime;
    }

    @Override
    public String getResultSummary() {
        if (this.exception != null) {
            StringWriter errorWrite = new StringWriter();
            this.exception.printStackTrace(new PrintWriter(errorWrite));
            return "Execute error:" + errorWrite;
        }
        return "execute success !!";
    }

    @Override
    public List<GraphQueryResultColumn> getColumns() {
        return this.resultSet.getColumnNames().stream().map(NebulaGraphQueryResultColumn::new).collect(Collectors.toList());
    }

    @Override
    public List<GraphQueryResultRow> getRows() {
        return IntStream.range(0, this.resultSet.rowsSize())
                .mapToObj(this.resultSet::rowValues)
                .map(NebulaGraphQueryResultRow::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<GraphNode> getNodes() {
        return Collections.emptyList();
    }

    @Override
    public List<GraphRelationship> getRelationships() {
        return getRows().stream().map(GraphQueryResultRow::getRelationships).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<GraphQueryNotification> getNotifications() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasPlan() {
        return false;
    }

    @Override
    public boolean isProfilePlan() {
        return false;
    }

    @Override
    public Optional<GraphQueryPlan> getPlan() {
        return Optional.empty();
    }
}
