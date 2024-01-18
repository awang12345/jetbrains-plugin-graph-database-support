package com.vesoft.jetbrains.plugin.graphdb.database.nebula;

import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.client.SessionPool;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaGraphMetadata;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.query.NebulaGraphQueryResult;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.meta.MetaManager;
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

    public NebulaDatabase(Map<String, String> config) {
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
        sessionPoolConfig.setMaxSessionSize(10);
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
        return executeInSession(sessionPool -> {
            try {
                final String specialSpace = sessionPool.getSpaceName();
                List<String> spaceNameList;
                if (specialSpace == null) {
                    Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get all spaces"));
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
                List<NebulaSpace> spaces = querySpaceList(sessionPool, spaceNameList, progressDetailDisplay, progressPercentageDisplay);
                return new NebulaGraphMetadata(spaceNameList.get(spaceNameList.size() - 1), spaces);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Execute nebula error", e);
            }
        });
    }

    private List<NebulaSpace> querySpaceList(SessionPool sessionPool, List<String> spaceNameList, Consumer<String> progressDetailDisplay, Consumer<Float> progressPercentageDisplay) throws Exception {

        List<NebulaSpace> spaces = new ArrayList<>();
        int size = spaceNameList.size();
        for (int i = 0; i < size; i++) {

            float percent = (float) i / size;
            Optional.ofNullable(progressPercentageDisplay).ifPresent(a -> a.accept(percent));

            String spaceName = spaceNameList.get(i);
            String useSpace = String.format(Consts.Stetments.USE_SPACE, spaceName);
            sessionPool.execute(useSpace);

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get all edge list for space:" + spaceName));
            List<NebulaEdge> edgeList = querySpaceEdgeList(progressDetailDisplay, spaceName, sessionPool);

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get all tag list for space:" + spaceName));
            List<NebulaTag> tagList = querySpaceTagList(progressDetailDisplay, spaceName, sessionPool);

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get ddl for space:" + spaceName));
            String ddl = getDDL(progressDetailDisplay, sessionPool, String.format(Consts.Stetments.SHOW_CREATE_SPACE, spaceName));

            spaces.add(new NebulaSpace(spaceName, edgeList, tagList, ddl));

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

    private List<NebulaEdge> querySpaceEdgeList(Consumer<String> progressDetailDisplay, String spaceName, SessionPool sessionPool) throws Exception {
        String useSpace = String.format(Consts.Stetments.SHOW_EDGES);
        ResultSet resultSet = sessionPool.execute(useSpace);
        List<NebulaEdge> edgeList = new ArrayList<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String edgeName = valueWrappers.get(0).asString();

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get property for edge:" + edgeName));
            Map<String, String> prop = getProp(progressDetailDisplay, sessionPool, String.format(Consts.Stetments.DESC_EDGE, edgeName));

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get ddl for edge:" + edgeName));
            String ddl = getDDL(progressDetailDisplay, sessionPool, String.format(Consts.Stetments.SHOW_CREATE_EDGE, edgeName));

            edgeList.add(new NebulaEdge(spaceName, edgeName, prop, ddl));
        }
        return edgeList;
    }

    private List<NebulaTag> querySpaceTagList(Consumer<String> progressDetailDisplay, String spaceName, SessionPool sessionPool) throws Exception {
        String useSpace = String.format(Consts.Stetments.SHOW_TAGS);
        ResultSet resultSet = sessionPool.execute(useSpace);
        List<NebulaTag> tagList = new ArrayList<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String tagName = valueWrappers.get(0).asString();

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get property for tag:" + tagName));
            Map<String, String> prop = getProp(progressDetailDisplay, sessionPool, String.format(Consts.Stetments.DESC_TAG, tagName));

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("get ddl for tag:" + tagName));
            String ddl = getDDL(progressDetailDisplay, sessionPool, String.format(Consts.Stetments.SHOW_CREATE_TAG, tagName));

            tagList.add(new NebulaTag(spaceName, tagName, prop, ddl));
        }
        return tagList;
    }

    private Map<String, String> getProp(Consumer<String> progressDetailDisplay, SessionPool sessionPool, String sql) throws Exception {
        ResultSet resultSet = sessionPool.execute(sql);
        Map<String, String> prop = new HashMap<>();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record valueWrappers = resultSet.rowValues(i);
            String fieldName = valueWrappers.get(0).asString();
            String type = valueWrappers.get(1).asString();
            prop.put(fieldName, type);
        }
        return prop;
    }

    private String getDDL(Consumer<String> progressDetailDisplay, SessionPool sessionPool, String sql) throws Exception {
        ResultSet resultSet = sessionPool.execute(sql);
        if (resultSet.rowsSize() == 0) {
            return "[ERROR]Get ddl statement failed from nebula.The query nGQL is " + sql;
        }
        ResultSet.Record valueWrappers = resultSet.rowValues(0);
        return valueWrappers.get(1).asString();
    }

    private MetaManager getMetaInfo(SessionPool sessionPool) {
        try {
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

    private SessionPool getSessionPool() {
        return Optional.ofNullable(sessionPool).orElseThrow(() -> new IllegalArgumentException("sessionPool is closed"));
    }

    @Override
    public void close() {
        this.sessionPool.close();
        this.sessionPool = null;
    }
}
