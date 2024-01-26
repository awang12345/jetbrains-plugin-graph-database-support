package com.vesoft.jetbrains.plugin.graphdb.database.nebula;

import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.client.SessionPool;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.*;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.query.NebulaGraphQueryResult;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.meta.MetaManager;
import com.vesoft.nebula.meta.SpaceDesc;
import com.vesoft.nebula.meta.SpaceItem;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NebulaDatabase implements GraphDatabaseApi {

    private SessionPool sessionPool;

    /**
     * 是否显示spaceId、tagId、edgeId
     */
    private final boolean isShowMetaId;

    public NebulaDatabase(Map<String, String> config) {
        this.isShowMetaId = config.getOrDefault("isShowMetaId", Boolean.TRUE.toString()).equals(Boolean.TRUE.toString());
        this.sessionPool = initSessionPool(config);
    }

    @Override
    public GraphQueryResult execute(String query) {
        return execute(query, null);
    }

    @Override
    public GraphQueryResult execute(String query, Map<String, Object> statementParameters) {
        return executeInSession(sessionPool -> {
            ResultSet resultSet;
            long startTime = System.currentTimeMillis();
            try {
                String ngql = new String(query.getBytes("UTF-8"));
                if (statementParameters != null && !statementParameters.isEmpty()) {
                    resultSet = sessionPool.execute(ngql, statementParameters);
                } else {
                    resultSet = sessionPool.execute(ngql);
                }
                if (resultSet.getErrorCode() != ErrorCode.SUCCEEDED.getValue()) {
                    throw new RuntimeException(String.format("[%s] Nebula nGQL execute fail. %s", resultSet.getSpaceName(), resultSet.getErrorMessage()));
                }
                return new NebulaGraphQueryResult(startTime, resultSet, null);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Nebula nGQL execute fail!nGQL=" + query, e);
            }
        });
    }

    private <T> T executeInSession(Function<SessionPool, T> executor) {
        return executor.apply(getSessionPool());
    }

    private SessionPool initSessionPool(Map<String, String> config) {
        NebulaConfiguration configuration = new NebulaConfiguration(config);
        List<HostAddress> addresses = Arrays.asList(new HostAddress(configuration.getHost(), configuration.getPort()));
        String spaceName = configuration.getDefaultSpace();
        String user = configuration.getUser();
        String password = configuration.getPassword();
        SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(addresses, StringUtils.defaultIfBlank(spaceName, SessionPool.NULL_SPACE), user, password);
        sessionPoolConfig.setMinSessionSize(1);
        sessionPoolConfig.setMaxSessionSize(1);
        sessionPoolConfig.setRetryTimes(3);
        sessionPoolConfig.setIntervalTime(1000);
        sessionPoolConfig.setWaitTime(10_000);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        if (!sessionPool.init()) {
            throw new RuntimeException("Nebula session init fail!!addresses=" + addresses);
        }
        return sessionPool;
    }

    @Override
    public NebulaGraphMetadata metadata(Consumer<String> progressDetailDisplay, Consumer<Float> progressPercentageDisplay) {
        final Consumer<String> progressDetailDisplay0 = Optional.ofNullable(progressDetailDisplay).orElse((s) -> {
        });
        final Consumer<Float> progressPercentageDisplay0 = Optional.ofNullable(progressPercentageDisplay).orElse((f) -> {
        });
        return executeInSession(sessionPool -> {
            try {
                Optional<MetaManager> managerOptional = Optional.empty();
                if (isShowMetaId) {
                    managerOptional = Optional.ofNullable(getMetaInfo(progressDetailDisplay0, sessionPool));
                }
                final String specialSpace = sessionPool.getSpaceName();
                List<String> spaceNameList;
                if (specialSpace == null) {
                    progressDetailDisplay0.accept("Fetch all spaces");
                    ResultSet resultSet = sessionPool.execute(Consts.Stetments.SHOW_SPACE);
                    if (resultSet.getErrorCode() != ErrorCode.SUCCEEDED.getValue()) {
                        throw new RuntimeException("Nebula nGQL execute fail !" + resultSet.getErrorMessage());
                    }
                    if (resultSet.rowsSize() == 0) {
                        return new NebulaGraphMetadata(resultSet.getSpaceName(), Collections.emptyList());
                    }
                    spaceNameList = IntStream.range(0, resultSet.rowsSize()).mapToObj(i -> valueToString(resultSet.rowValues(i).get(0))).collect(Collectors.toList());
                } else {
                    spaceNameList = Arrays.asList(specialSpace);
                }
                List<NebulaSpace> spaces = querySpaceList(sessionPool, spaceNameList, managerOptional, progressDetailDisplay0, progressPercentageDisplay0);
                return new NebulaGraphMetadata(spaceNameList.get(spaceNameList.size() - 1), spaces);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Execute nebula error", e);
            }
        });
    }

    private List<NebulaSpace> querySpaceList(SessionPool sessionPool, List<String> spaceNameList, Optional<MetaManager> managerOptional, Consumer<String> progressDetailDisplay, Consumer<Float> progressPercentageDisplay) throws Exception {

        List<NebulaSpace> spaces = new ArrayList<>();
        int size = spaceNameList.size();
        for (int i = 0; i < size; i++) {

            float percent = (float) i / size;
            progressPercentageDisplay.accept(percent);

            String spaceName = spaceNameList.get(i);
            String useSpace = String.format(Consts.Stetments.USE_SPACE, spaceName);
            sessionPool.execute(useSpace);

            Map<String, Long> spaceStats = getSpaceStats(sessionPool);

            progressDetailDisplay.accept("Fetch all edge list for space:" + spaceName);
            List<NebulaEdge> edgeList = querySpaceEdgeList(progressDetailDisplay, spaceName, sessionPool, managerOptional);

            progressDetailDisplay.accept("Fetch all tag list for space:" + spaceName);
            List<NebulaTag> tagList = querySpaceTagList(progressDetailDisplay, spaceName, sessionPool, managerOptional);

            progressDetailDisplay.accept("Fetch ddl for space:" + spaceName);
            String ddl = getDDL(sessionPool, String.format(Consts.Stetments.SHOW_CREATE_SPACE, spaceName));

            NebulaSpace nebulaSpace = new NebulaSpace(spaceName, edgeList, tagList, ddl);
            managerOptional.ifPresent(metaManager -> {
                SpaceItem space = metaManager.getSpace(spaceName);
                nebulaSpace.setId(String.valueOf(space.space_id));
                SpaceDesc properties = space.getProperties();
                nebulaSpace.setType(properties.vid_type.getType().name());
            });

            if (!spaceStats.isEmpty()) {
                edgeList.forEach(edge -> edge.setDataCount(spaceStats.get(edge.getEdgeName())));
                tagList.forEach(tag -> tag.setDataCount(spaceStats.get(tag.getTagName())));
                Long vertices = spaceStats.get("vertices");
                Long edges = spaceStats.get("edges");
                if (vertices != null || edges != null) {
                    nebulaSpace.setDataCount(Optional.ofNullable(vertices).orElse(0L) + Optional.ofNullable(edges).orElse(0L));
                }
            }

            spaces.add(nebulaSpace);

        }
        return spaces;
    }

    private String valueToString(ValueWrapper valueWrapper) {
        try {
            return valueWrapper.asString();
        } catch (UnsupportedEncodingException e) {
            return new String(valueWrapper.getValue().getSVal(), StandardCharsets.UTF_8);
        }
    }

    private List<NebulaEdge> querySpaceEdgeList(Consumer<String> progressDetailDisplay, String spaceName, SessionPool sessionPool, Optional<MetaManager> managerOptional) throws Exception {
        String useSpace = String.format(Consts.Stetments.SHOW_EDGES);
        ResultSet resultSet = sessionPool.execute(useSpace);
        List<NebulaEdge> edgeList = new ArrayList<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String edgeName = valueWrappers.get(0).asString();

            progressDetailDisplay.accept("Fetch property for edge:" + edgeName);
            List<NebulaField> prop = getProp(sessionPool, String.format(Consts.Stetments.DESC_EDGE, edgeName));

            progressDetailDisplay.accept("Fetch ddl for edge:" + edgeName);
            String ddl = getDDL(sessionPool, String.format(Consts.Stetments.SHOW_CREATE_EDGE, edgeName));

            NebulaEdge edge = new NebulaEdge(spaceName, edgeName, prop, ddl);

            managerOptional.ifPresent(manager -> {
                int edgeType = manager.getEdgeType(spaceName, edgeName);
                edge.setId(String.valueOf(edgeType));
            });

            edgeList.add(edge);
        }
        return edgeList;
    }

    private List<NebulaTag> querySpaceTagList(Consumer<String> progressDetailDisplay, String spaceName, SessionPool sessionPool, Optional<MetaManager> managerOptional) throws Exception {
        String useSpace = String.format(Consts.Stetments.SHOW_TAGS);
        ResultSet resultSet = sessionPool.execute(useSpace);
        List<NebulaTag> tagList = new ArrayList<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String tagName = valueWrappers.get(0).asString();

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("Fetch property for tag:" + tagName));
            List<NebulaField> prop = getProp(sessionPool, String.format(Consts.Stetments.DESC_TAG, tagName));

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("Fetch ddl for tag:" + tagName));
            String ddl = getDDL(sessionPool, String.format(Consts.Stetments.SHOW_CREATE_TAG, tagName));

            NebulaTag tag = new NebulaTag(spaceName, tagName, prop, ddl);

            managerOptional.ifPresent(manager -> {
                int tagId = manager.getTagId(spaceName, tagName);
                tag.setId(String.valueOf(tagId));
            });

            tagList.add(tag);
        }
        return tagList;
    }

    private List<NebulaField> getProp(SessionPool sessionPool, String sql) throws Exception {
        ResultSet resultSet = sessionPool.execute(sql);
        int fieldIdx = 0;
        int typeIdx = 1;
        int commentIdx = 4;
        List<String> columnNames = resultSet.getColumnNames();
        for (int i = 0; i < columnNames.size(); i++) {
            switch (columnNames.get(i).toLowerCase()) {
                case "field":
                    fieldIdx = i;
                    break;
                case "type":
                    typeIdx = i;
                    break;
                case "comment":
                    commentIdx = i;
                    break;
                default:
                    break;
            }
        }
        List<NebulaField> fieldList = new ArrayList<>(resultSet.rowsSize());
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String fieldName = valueWrappers.get(fieldIdx).asString();
            String type = valueWrappers.get(typeIdx).asString();
            String comment = null;
            ValueWrapper commentValue = valueWrappers.get(commentIdx);
            if (!commentValue.isEmpty() && !commentValue.isNull()) {
                comment = commentValue.asString();
            }
            fieldList.add(new NebulaField(fieldName, type, comment));
        }
        return fieldList;
    }

    private String getDDL(SessionPool sessionPool, String sql) throws Exception {
        ResultSet resultSet = sessionPool.execute(sql);
        if (resultSet.rowsSize() == 0) {
            return "[ERROR]Get ddl statement failed from nebula.The query nGQL is " + sql;
        }
        ResultSet.Record valueWrappers = resultSet.rowValues(0);
        return valueWrappers.get(1).asString();
    }

    private MetaManager getMetaInfo(Consumer<String> progressDetailDisplay, SessionPool sessionPool) {
        try {
            progressDetailDisplay.accept("Fetch meta server address");
            HostAddress metaAddress = getMetaAddress(sessionPool);
            MetaManager metaManager = new MetaManager(Arrays.asList(metaAddress), 3000, 1, 1, false, null);
            return metaManager;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private HostAddress getMetaAddress(SessionPool sessionPool) throws Exception {
        String sql = "SHOW META LEADER";
        ResultSet resultSet = sessionPool.execute(sql);
        if (resultSet.rowsSize() == 0) {
            return null;
        }
        ResultSet.Record valueWrappers = resultSet.rowValues(0);
        String address = valueWrappers.get(0).asString();
        String[] metaHostPort = StringUtils.split(address, ":");
        String host = metaHostPort[0];
        String port = metaHostPort[1];
        if (host.equals(sessionPool.getAddress().getHost())
                || !StringUtils.startsWithAny(host, new String[]{"localhost", "127.0.0.1"})) {
            return new HostAddress(host, Integer.parseInt(port));
        }
        return new HostAddress(sessionPool.getAddress().getHost(), Integer.parseInt(port));
    }

    private Map<String, Long> getSpaceStats(SessionPool sessionPool) throws Exception {
        ResultSet resultSet = sessionPool.execute(Consts.Stetments.SHOW_STATS);
        if (resultSet.isEmpty()) {
            return Collections.emptyMap();
        }
        int nameIdx = 1;
        int countIdx = 2;
        List<String> columnNames = resultSet.getColumnNames();
        for (int i = 0; i < columnNames.size(); i++) {
            switch (columnNames.get(i).toLowerCase()) {
                case "name":
                    nameIdx = i;
                    break;
                case "count":
                    countIdx = i;
                    break;
                default:
                    break;
            }
        }
        Map<String, Long> spaceStats = new HashMap<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String name = valueWrappers.get(nameIdx).asString();
            Long count = valueWrappers.get(countIdx).asLong();
            spaceStats.put(name, count);
        }
        return spaceStats;
    }

    private SessionPool getSessionPool() {
        return Optional.ofNullable(sessionPool).orElseThrow(() -> new IllegalArgumentException("sessionPool is closed"));
    }

    @Override
    public void close() {
        this.sessionPool.close();
        this.sessionPool = null;
    }
}
