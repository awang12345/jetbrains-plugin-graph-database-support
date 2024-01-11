package com.vesoft.jetbrains.plugin.graphdb.database.nebula.query;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.*;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphRelationship;
import com.vesoft.nebula.client.graph.data.ResultSet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
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

    private Map<String, GraphNode> nodeMap = new HashMap<>();

    private List<GraphRelationship> relationshipList = new ArrayList<>();

    private List<GraphQueryResultRow> rowList;

    private List<GraphQueryResultColumn> queryResultColumns;

    private String currentSpace;

    public NebulaGraphQueryResult(long startTime, ResultSet resultSet, Exception exception) {
        this.costTime = System.currentTimeMillis() - startTime;
        this.resultSet = resultSet;
        this.exception = exception;
        this.currentSpace = resultSet.getSpaceName();

        this.rowList = IntStream.range(0, this.resultSet.rowsSize())
                .mapToObj(this.resultSet::rowValues)
                .map(NebulaGraphQueryResultRow::new)
                .collect(Collectors.toList());

        this.queryResultColumns = this.resultSet.getColumnNames().stream().map(NebulaGraphQueryResultColumn::new).collect(Collectors.toList());

        initNode();
        initRelationShip();
    }

    private void initNode() {
        for (GraphQueryResultRow resultRow : this.rowList) {
            List<GraphNode> nodes = resultRow.getNodes();
            if (nodes != null) {
                nodes.forEach(node -> {
                    GraphNode graphNode = nodeMap.get(node.getId());
                    if (graphNode == null) {
                        nodeMap.put(node.getId(), node);
                    } else {
                        Map<String, Object> properties = graphNode.getPropertyContainer().getProperties();
                        if (properties == null || properties.isEmpty()) {
                            nodeMap.put(node.getId(), node);
                        }
                    }
                });
            }
        }
    }

    private void initRelationShip() {
        this.relationshipList = this.rowList.stream().map(GraphQueryResultRow::getRelationships).flatMap(List::stream)
                .peek(graphRelationship -> {
                    GraphNode startNode = nodeMap.get(graphRelationship.getStartNodeId());
                    Optional.ofNullable(startNode).ifPresent(((NebulaGraphRelationship) graphRelationship)::setStartNode);
                    GraphNode endNode = nodeMap.get(graphRelationship.getEndNodeId());
                    Optional.ofNullable(endNode).ifPresent(((NebulaGraphRelationship) graphRelationship)::setEndNode);
                }).collect(Collectors.toList());
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
        return this.queryResultColumns;
    }

    @Override
    public List<GraphQueryResultRow> getRows() {
        return this.rowList;
    }

    @Override
    public List<GraphNode> getNodes() {
        return new ArrayList<>(this.nodeMap.values());
    }

    @Override
    public List<GraphRelationship> getRelationships() {
        return this.relationshipList;
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

    public String getCurrentSpace() {
        return currentSpace;
    }
}
